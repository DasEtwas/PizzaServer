package io.github.willqi.pizzaserver.server.network.protocol.versions;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.willqi.pizzaserver.commons.utils.Tuple;
import io.github.willqi.pizzaserver.format.BlockRuntimeMapper;
import io.github.willqi.pizzaserver.nbt.streams.nbt.NBTInputStream;
import io.github.willqi.pizzaserver.nbt.streams.varint.VarIntDataInputStream;
import io.github.willqi.pizzaserver.nbt.tags.NBTCompound;
import io.github.willqi.pizzaserver.server.Server;
import io.github.willqi.pizzaserver.server.network.protocol.data.ItemState;
import io.github.willqi.pizzaserver.server.world.blocks.types.BlockType;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public abstract class MinecraftVersion implements BlockRuntimeMapper {

    private final static Gson GSON = new Gson();

    private final Server server;

    private NBTCompound biomesDefinitions;
    private final Map<Tuple<String, NBTCompound>, Integer> blockStates = new HashMap<>();
    private Set<ItemState> itemStates;


    public MinecraftVersion(Server server) throws IOException {
        this.server = server;
        this.loadBiomeDefinitions();
        this.loadBlockStates();
        this.loadRuntimeItems();
    }

    public abstract int getProtocol();

    public abstract String getVersionString();

    public abstract PacketRegistry getPacketRegistry();

    protected void loadBiomeDefinitions() throws IOException {
        try (NBTInputStream biomesNBTStream = new NBTInputStream(
                new VarIntDataInputStream(this.getProtocolResourceStream("biome_definitions.nbt"))
        )) {
            this.biomesDefinitions = biomesNBTStream.readCompound();
        }
    }

    protected void loadBlockStates() throws IOException {
        this.blockStates.clear();
        try (NBTInputStream blockStatesNBTStream = new NBTInputStream(
                new VarIntDataInputStream(this.getProtocolResourceStream("block_states.nbt"))
        )) {
            // keySet returns in ascending rather than descending so we have to reverse it
            SortedMap<String, LinkedHashSet<NBTCompound>> blockStates = new TreeMap<>(Collections.reverseOrder((fullBlockIdA, fullBlockIdB) -> {
                // Runtime ids are mapped by their part b first before part a (e.g. b:b goes before b:c and a:d)
                String blockIdA = fullBlockIdA.substring(fullBlockIdA.indexOf(":") + 1);
                String blockIdB = fullBlockIdB.substring(fullBlockIdB.indexOf(":") + 1);
                int blockIdComparison = blockIdB.compareTo(blockIdA);
                if (blockIdComparison != 0) {
                    return blockIdComparison;
                }
                // Compare by namespace
                String namespaceA = fullBlockIdA.substring(0, fullBlockIdA.indexOf(":"));
                String namespaceB = fullBlockIdB.substring(0, fullBlockIdB.indexOf(":"));
                return namespaceB.compareTo(namespaceA);
            }));

            // Parse block states
            while (blockStatesNBTStream.available() > 0) {
                NBTCompound blockState = blockStatesNBTStream.readCompound();

                String name = blockState.getString("name");
                if (!blockStates.containsKey(name)) {
                    blockStates.put(name, new LinkedHashSet<>());
                }

                NBTCompound states = blockState.getCompound("states");
                blockStates.get(name).add(states);
            }

            // Add custom block states
            for (BlockType blockType : this.server.getBlockRegistry().getCustomTypes()) {
                blockStates.put(blockType.getBlockId(), new LinkedHashSet<>(blockType.getBlockStates().keySet()));
            }

            // Construct runtime ids
            int runtimeId = 0;
            for (String blockId : blockStates.keySet()) {
                for (NBTCompound states : blockStates.get(blockId)) {
                    this.blockStates.put(new Tuple<>(blockId, states), runtimeId++);
                }
            }

            // debug logging
            this.logBlockStates(blockStates);
        }
    }

    /**
     * Creates a file and displays NBT data for all missing block states
     * Used for debugging new unimplemented block states
     * @param blockStates
     */
    private void logBlockStates(Map<String, LinkedHashSet<NBTCompound>> blockStates) {
        if (this.server.getConfig().isDebugActive()) {
            File protocolFolder = Paths.get(this.server.getRootDirectory(), "debug", "blocks", String.valueOf(this.getProtocol())).toFile();
            protocolFolder.mkdirs();

            boolean foundMissedBlockStates = false;

            for (String minecraftId : blockStates.keySet()) {
                File blockFile = Paths.get(protocolFolder.getAbsolutePath(), minecraftId.replace(':', '_')).toFile();
                blockFile.delete(); // delete existing log file from file system if it exists

                Set<NBTCompound> missingBlockStates = new LinkedHashSet<>();
                if (this.server.getBlockRegistry().hasBlockType(minecraftId)) {
                    // check if existing implementation is missing any block states
                    for (NBTCompound blockState : blockStates.get(minecraftId)) {
                        if (this.server.getBlockRegistry().getBlockType(minecraftId).getBlockStateIndex(blockState) == -1) {
                            missingBlockStates.add(blockState);
                        }
                    }
                } else {
                    // they don't have any block states of this type
                    missingBlockStates.addAll(blockStates.get(minecraftId));
                }

                // Are any block states missing?
                if (missingBlockStates.size() > 0) {
                    foundMissedBlockStates = true;

                    try (FileWriter writer = new FileWriter(blockFile)) {
                        StringBuilder blockStatesStr = new StringBuilder();
                        for (NBTCompound blockState : missingBlockStates) {
                            blockStatesStr.append(blockState.toString() + "\n");
                        }

                        writer.write(
                                "Missing block states for block id: " + minecraftId + "\n" +
                                        blockStatesStr.toString());
                    } catch (IOException exception) {
                        this.server.getLogger().error("Failed to write block state file for " + minecraftId + " when logging unimplemented block states.");
                    }
                }
            }

            if (foundMissedBlockStates) {
                this.server.getLogger().warn("Not all block states of v" + this.getProtocol() + " are implemented.");
            }


        }
    }

    protected void loadRuntimeItems() throws IOException {
        try (Reader itemStatesReader = new InputStreamReader(this.getProtocolResourceStream("runtime_item_states.json"))) {
            JsonArray jsonItemStates = GSON.fromJson(itemStatesReader, JsonArray.class);

            Set<ItemState> itemStates = new HashSet<>(jsonItemStates.size());
            for (int i = 0; i < jsonItemStates.size(); i++) {
                JsonObject jsonItemState = jsonItemStates.get(i).getAsJsonObject();

                itemStates.add(new ItemState(
                        jsonItemState.get("name").getAsString(),
                        jsonItemState.get("id").getAsInt(),
                        false));
            }
            this.itemStates = itemStates;
        }
    }

    protected InputStream getProtocolResourceStream(String fileName) {
        return Server.getInstance().getClass().getResourceAsStream("/protocol/v" + this.getProtocol() + "/" + fileName);
    }

    @Override
    public int getBlockRuntimeId(String name, NBTCompound state) {
        try {
            return this.blockStates.get(new Tuple<>(name, state));
        } catch (NullPointerException exception) {
            throw new NullPointerException("Failed to find block runtime id for a state of " + name);
        }
    }

    public Set<ItemState> getItemStates() {
        return Collections.unmodifiableSet(this.itemStates);
    }

    public NBTCompound getBiomeDefinitions() {
        return this.biomesDefinitions;
    }
}
