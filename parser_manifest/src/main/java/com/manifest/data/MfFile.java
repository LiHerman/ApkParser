package com.manifest.data;

import com.common.LogUtil;
import com.common.PrintUtil;
import com.manifest.stream.LittleEndianStreamer;
import com.manifest.stream.MfStreamer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by xueqiulxq on 16/07/2017.
 */

public class MfFile {

    private static final String STRING_CHUNK_TYPE = "StringChunk";
    private static final String RESOURCE_ID_CHUNK_TYPE = "ResourceIdChunk";
    private static final String START_NAMESPACE_CHUNK_TYPE = "StartNamespaceChunk";
    private static final String START_TAG_CHUNK_TYPE = "StartTagChunk";
    private static final String END_TAG_CHUNK_TYPE = "EndTagChunk";
    private static final String UNKNOWN_CHUNK_TYPE = "UnknownChunk";
    public static final int STRING_CHUNK_ID = 0x001C0001;
    public static final int RESOURCE_ID_CHUNK_ID = 0x00080180;
    public static final int START_NAMESPACE_CHUNK_ID = 0x00100100;
    public static final int START_TAG_CHUNK_ID = 0x00100102;
    public static final int EDN_TAG_CHUNK_ID = 0x00100103;

    private MfStreamer mStreamer;
    public MfHeader header;
    public StringChunk stringChunk;
    public ResourceIdChunk resourceIdChunk;
    public StartNamespaceChunk startNamespaceChunk;
    public List<StartTagChunk> startTagChunks;
    public List<EndTagChunk> endTagChunks;

    public MfFile() {
        startTagChunks = new ArrayList<StartTagChunk>();
        endTagChunks = new ArrayList<EndTagChunk>();
    }

    public void parse(RandomAccessFile racFile) throws IOException {
        racFile.seek(0);
        mStreamer = new LittleEndianStreamer();

        byte[] headerBytes = new byte[MfHeader.LENGTH];
        racFile.read(headerBytes, 0, headerBytes.length);
        header = parseHeader(headerBytes);

        int cursor = MfHeader.LENGTH;
        do {
            byte[] infoBytes = new byte[ChunkInfo.LENGTH];
            cursor += racFile.read(infoBytes, 0, infoBytes.length);
            mStreamer.use(infoBytes);
            ChunkInfo info = ChunkInfo.parseFrom(mStreamer);

            // Chunk size = ChunkInfo + BodySize
            byte[] chunkBytes = new byte[(int)info.chunkSize];
            System.arraycopy(infoBytes, 0, chunkBytes, 0, ChunkInfo.LENGTH);
            cursor += racFile.read(chunkBytes, ChunkInfo.LENGTH, (int)info.chunkSize - ChunkInfo.LENGTH);
            String chunkType = UNKNOWN_CHUNK_TYPE;
            switch ((int)info.chunkType) {
                case STRING_CHUNK_ID:
                    chunkType = STRING_CHUNK_TYPE;
                    stringChunk = parseStringChunk(chunkBytes);
                    break;
                case RESOURCE_ID_CHUNK_ID:
                    chunkType = RESOURCE_ID_CHUNK_TYPE;
                    resourceIdChunk = parseResourceIdChunk(chunkBytes);
                    break;
                case START_NAMESPACE_CHUNK_ID:
                    chunkType = START_NAMESPACE_CHUNK_TYPE;
                    startNamespaceChunk = parseStartNamespaceChunk(chunkBytes);
                    break;
                case START_TAG_CHUNK_ID:
                    startTagChunks.add(parseStartTagChunk(chunkBytes));
                    chunkType = START_TAG_CHUNK_TYPE;
                    break;
                case EDN_TAG_CHUNK_ID:
                    endTagChunks.add(parseEndTagChunk(chunkBytes));
                    chunkType = END_TAG_CHUNK_TYPE;
            }
            LogUtil.i(String.format("%s %s", PrintUtil.hex4(info.chunkType), chunkType));
        } while (cursor < header.fileLength);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(4096);
        builder.append(header).append('\n');
        builder.append(stringChunk).append('\n');
        builder.append(resourceIdChunk).append('\n');
        builder.append(startNamespaceChunk).append('\n');

        builder.append("-- StartTag Chunks --").append('\n');
        for (int i=0; i<startTagChunks.size(); ++i) {
            builder.append("StartTagChunk_").append(i).append('\n');
            builder.append(startTagChunks.get(i)).append('\n');
        }
        builder.append("-- EndTag Chunks --").append('\n');
        for (int i=0; i<endTagChunks.size(); ++i) {
            builder.append("EndTagChunk_").append(i).append('\n');
            builder.append(endTagChunks.get(i)).append('\n');
        }
        return builder.toString();
    }

    public MfHeader parseHeader(byte[] data) throws IOException {
        mStreamer.use(data);
        return MfHeader.parseFrom(mStreamer);
    }

    public StringChunk parseStringChunk(byte[] chunkData) throws IOException {
        mStreamer.use(chunkData);
        return StringChunk.parseFrom(mStreamer);
    }

    public ResourceIdChunk parseResourceIdChunk(byte[] chunkData) throws IOException {
        mStreamer.use(chunkData);
        return ResourceIdChunk.parseFrom(mStreamer);
    }

    public StartNamespaceChunk parseStartNamespaceChunk(byte[] chunkData) {
        mStreamer.use(chunkData);
        return StartNamespaceChunk.parseFrom(mStreamer, stringChunk);
    }

    public StartTagChunk parseStartTagChunk(byte[] chunkData) {
        mStreamer.use(chunkData);
        return StartTagChunk.parseFrom(mStreamer, stringChunk);
    }

    public EndTagChunk parseEndTagChunk(byte[] chunkData) {
        mStreamer.use(chunkData);
        return EndTagChunk.parseFrom(mStreamer, stringChunk);
    }
}
