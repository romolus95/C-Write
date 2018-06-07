package cards;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class TiledCard {

    private Map<String, Vector[][]> tiles;
    private int row;
    private int col;
    private int posZeroLon;
    private int posZeroLat;
    private int distanceBtwVector_X;
    private int distanceBtwVector_Y;
    private int northOffset;
    private int overlap;
    private int tileCount;
    private String directoryPath = "";

    private enum Tag{
        TILES_X(0x01, "tiles_x"), TILES_Y(0x02, "tiles_y"), POSITION_ZERO_LON(0x03, "pos_zero_low"), POSITION_ZERO_LAT(0x04, "pos_zero_lat"),
        DISTANCE_VECTOR_X(0x06, "dist_vect_x"), DISTANCE_VECTOR_Y(0x07, "dist_vect_y"), OVERLAP_TILES(0x08, "overlap"),
        COMPASS_OFFSET(0x09, "comp_offset"), AMOUNT_TILES(0x0A, "tile_count"), TILE_X(0x0B, "tile_x"), TILE_Y(0x0C, "tile_y"),
        TILE_ZERO_LON(0x0D, "tile_zero_lon"), TILE_ZERO_LAT(0x0E, "tile_zero_lat"), TILE(0x0D, "tile"), VECTOR(0x10, "vector");

        private final int id;
        private final String name;
        Tag(int id, String name){
            this.id = id;
            this.name = name;
        }
        public int getId(){return id;}

        public String getName() {
            return name;
        }
    }

    public TiledCard(int row, int col, int posZeroLon, int posZeroLat, int distanceBtwVector_X, int distanceBtwVector_Y, int northOffset, int overlap){
        this.row = row;
        this.col = col;
        this.tiles = new HashMap<>();
        this.posZeroLon = posZeroLon;
        this.posZeroLat = posZeroLat;
        this.distanceBtwVector_X = distanceBtwVector_X;
        this.distanceBtwVector_Y = distanceBtwVector_Y;
        this.northOffset = northOffset;
        this.overlap = overlap;
    }

    public void tileCard(UntiledCard untiledCard){
        Vector[][] vectors = untiledCard.getVectors();
        int tileRow = vectors.length/row;
        int tileCol = vectors[0].length/col;
        int tileX = 0;
        int tileY = 0;
        tileCount = vectors.length/tileRow * vectors[0].length/tileCol;
        for(int c = 0; c < tileCount; c++){
            Vector[][] tile = new Vector[tileRow][tileCol];
            int startX = (tileRow * (c/tileRow)) % vectors.length;
            int startY = (tileCol * c) % vectors[0].length;

            if(startY + tileCol > vectors[0].length){
                startY = 0;
            }
            for(int i = 0; i < tileRow; i++){
                for (int j = 0; j < tileCol; j++){
                    tile[i][j] = vectors[startX+i][startY+j];
                }
            }
            String key = "_" + tileX + "_" + tileY++;
            tiles.put(key,tile);
            if(tileY == col){
                tileY=0;
                tileX+=1;
            }
        }
    }

    private int calculateLatProjection(int centimeters, int angle){
        double meters = (double)centimeters/(double) 100;
        double realDistance = Math.sin(Math.toDegrees(angle)) * meters;
        double projection = realDistance/((double) 1250 * Math.cos(Math.toDegrees(51.2)));
        int realProjection = (int) projection;
        return realProjection;
    }

    public void setDirectoryPath(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public void writeInfoFile() throws Exception{
        String fileName = "CardInfo.vft";
        StringJoiner joiner = new StringJoiner(";\n");
        BufferedWriter writer = new BufferedWriter(new FileWriter(directoryPath + fileName));
        joiner.add(Tag.TILES_X.name + ":" + row);
        joiner.add(Tag.TILES_Y.name + ":" + col);
        joiner.add(Tag.POSITION_ZERO_LON.name + ":" + posZeroLon);
        joiner.add(Tag.POSITION_ZERO_LAT.name + ":" + posZeroLat);
        joiner.add(Tag.DISTANCE_VECTOR_X.name + ":" + distanceBtwVector_X);
        joiner.add(Tag.DISTANCE_VECTOR_Y.name + ":" + distanceBtwVector_Y);
        joiner.add(Tag.OVERLAP_TILES.name + ":" + overlap);
        joiner.add(Tag.COMPASS_OFFSET.name + ":" + northOffset);
        joiner.add(Tag.AMOUNT_TILES.name + ":" + tileCount);
        writer.write(joiner.toString());
        writer.close();
    }

    public void writeInfoFileBinary() throws Exception{
        String fileName = "CardInfo.vft";
        StringJoiner joiner = new StringJoiner("\n");
        BufferedWriter writer = new BufferedWriter(new FileWriter(directoryPath + fileName));
        joiner.add(conversion8Bit(Tag.TILES_X.getId()) + conversion8Bit(row));
        joiner.add(conversion8Bit(Tag.TILES_Y.getId()) + conversion8Bit(col) );
        joiner.add(conversion8Bit(Tag.POSITION_ZERO_LON.getId()) + conversion32Bit(posZeroLon));
        joiner.add(conversion8Bit(Tag.POSITION_ZERO_LAT.getId()) + conversion32Bit(posZeroLat));
        joiner.add(conversion8Bit(Tag.DISTANCE_VECTOR_X.getId()) + conversion16Bit(distanceBtwVector_X));
        joiner.add(conversion8Bit(Tag.DISTANCE_VECTOR_Y.getId()) + conversion16Bit(distanceBtwVector_Y));
        joiner.add(conversion8Bit(Tag.OVERLAP_TILES.getId()) + conversion8Bit(overlap));
        joiner.add(conversion8Bit(Tag.COMPASS_OFFSET.getId()) + conversion8Bit(northOffset));
        joiner.add(conversion8Bit(Tag.AMOUNT_TILES.getId()) + conversion8Bit(tileCount));
        writer.write(joiner.toString());
        writer.close();
    }

    public void writeTileFiles() throws Exception{
        String fileName = "Tl";
        String fileExtension = ".vft";
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                String key = "_" + i + "_" + j;
                StringJoiner joiner = new StringJoiner("\n");
                Vector[][] vectors = tiles.get(key);
                joiner.add(Tag.TILE_X.name + ":" + vectors.length + ";");
                joiner.add(Tag.TILE_Y.name + ":" + vectors[0].length + ";");
                joiner.add(Tag.TILE_ZERO_LON.name + ":" + 0);
                joiner.add(Tag.TILE_ZERO_LAT.name + ":" + 0);
                joiner.add(Tag.TILE.name + ":[");
                for(int h = 0; h < vectors.length; h++){
                    for(int k = 0; k < vectors[h].length; k++){
                        joiner.add(Tag.VECTOR.name + ":" + vectors[h][k].toString());
                    }
                }
                joiner.add("]");
                BufferedWriter writer = new BufferedWriter(new FileWriter(directoryPath + fileName + key + fileExtension));
                writer.write(joiner.toString());
                writer.close();
            }
        }
    }

    private String conversion8Bit(int dec){
        return dec <= 0xFF ? String.format("%8s", Integer.toBinaryString(dec)).replace(' ', '0') : null;
    }

    private String conversion16Bit(int dec){
        return dec <= 0xFFFF ? String.format("%16s", Integer.toBinaryString(dec)).replace(' ', '0') : null;
    }

    private String conversion32Bit(int dec){
        return dec <= 0xFFFFFFFF ? String.format("%32s", Integer.toBinaryString(dec)).replace(' ', '0') : null;
    }



    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
            for(String key: tiles.keySet()){
                Vector[][] v = tiles.get(key);
                builder.append(key+"\n");
                for(int i = 0; i < v.length; i++){
                    for(int j = 0; j < v[i].length; j++){
                        builder.append(v[i][j].toString());
                    }
                    builder.append("\n");
                }
                builder.append("\n");
            }
        return builder.toString();
    }
}
