/**
 *
 * NORD POS is a fork of Openbravo POS.
 *
 * Copyright (C) 2009-2013 Nord Trading Ltd. <http://www.nordpos.com>
 *
 * This file is part of NORD POS.
 *
 * NORD POS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * NORD POS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * NORD POS. If not, see <http://www.gnu.org/licenses/>.
 */
package com.nordpos.device.escpos;

import com.nordpos.device.writter.Writter;
import com.nordpos.device.receiptprinter.DevicePrinter;
import java.awt.image.BufferedImage;
import com.openbravo.pos.util.BarcodeString;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3.0
 */
public abstract class Procedures {

    public abstract byte[] getInitSequence();

    public abstract byte[] getSize0();

    public abstract byte[] getSize1();

    public abstract byte[] getSize2();

    public abstract byte[] getSize3();

    public abstract byte[] getBoldSet();

    public abstract byte[] getBoldReset();

    public abstract byte[] getUnderlineSet();

    public abstract byte[] getUnderlineReset();

    public abstract byte[] getOpenDrawer();

    public abstract byte[] getCutReceipt();

    public abstract byte[] getNewLine();

    public abstract byte[] getImageHeader();

    public abstract int getImageWidth();

    public void printBarcode(Writter out, String type, String position, String code) throws UnsupportedEncodingException {

        if (DevicePrinter.BARCODE_EAN13.equals(type)) {

            out.write(getNewLine());

            out.write(Commands.BAR_HEIGHT);
            if (DevicePrinter.POSITION_NONE.equals(position)) {
                out.write(Commands.BAR_POSITIONNONE);
            } else if (DevicePrinter.POSITION_TOP.equals(position)) {
                out.write(Commands.BAR_POSITIONUP);
            } else {
                out.write(Commands.BAR_POSITIONDOWN);
            }
            out.write(Commands.BAR_HRIFONT1);
            out.write(Commands.BAR_EAN13);
            out.write(BarcodeString.getBarcodeStringEAN13(code).getBytes("ASCII"));
            out.write(new byte[]{0x00});
            out.write(getNewLine());
        } else if (DevicePrinter.BARCODE_EAN8.equals(type)) {
            out.write(getNewLine());
            out.write(Commands.BAR_HEIGHT);
            if (DevicePrinter.POSITION_NONE.equals(position)) {
                out.write(Commands.BAR_POSITIONNONE);
            } else if (DevicePrinter.POSITION_TOP.equals(position)) {
                out.write(Commands.BAR_POSITIONUP);
            } else {
                out.write(Commands.BAR_POSITIONDOWN);
            }
            out.write(Commands.BAR_HRIFONT1);
            out.write(Commands.BAR_EAN8);
            out.write(BarcodeString.getBarcodeStringEAN8(code).getBytes("ASCII"));
            out.write(new byte[]{0x00});
            out.write(getNewLine());
        } else if (DevicePrinter.BARCODE_CODE39.equals(type)) {
            out.write(getNewLine());
            out.write(Commands.BAR_HEIGHT);
            if (DevicePrinter.POSITION_NONE.equals(position)) {
                out.write(Commands.BAR_POSITIONNONE);
            } else if (DevicePrinter.POSITION_TOP.equals(position)) {
                out.write(Commands.BAR_POSITIONUP);
            } else {
                out.write(Commands.BAR_POSITIONDOWN);
            }
            out.write(Commands.BAR_HRIFONT1);
            out.write(Commands.BAR_CODE39);
            out.write(BarcodeString.getBarcodeStringCode39(code).getBytes("ASCII"));
            out.write(new byte[]{0x00});
            out.write(getNewLine());
        } else if (DevicePrinter.BARCODE_CODE128.equals(type)) {
            out.write(getNewLine());
            out.write(Commands.BAR_HEIGHT);
            if (DevicePrinter.POSITION_NONE.equals(position)) {
                out.write(Commands.BAR_POSITIONNONE);
            } else if (DevicePrinter.POSITION_TOP.equals(position)) {
                out.write(Commands.BAR_POSITIONUP);
            } else {
                out.write(Commands.BAR_POSITIONDOWN);
            }
            out.write(Commands.BAR_HRIFONT1);
            out.write(Commands.BAR_CODE128);
            String CompleteBarcode = BarcodeString.getBarcodeStringCode128(code);
            out.write(new byte[]{(byte) (CompleteBarcode.length() + 2)}); // 2 bytes extra for code set selection
            out.write(Commands.BAR_CODE_CODE128_A);
            out.write(CompleteBarcode.getBytes("ASCII"));
            out.write(getNewLine());
        }
    }

    public byte[] transImage(BufferedImage image) {

        CenteredImage centeredimage = new CenteredImage(image, getImageWidth());

        int iWidth = (centeredimage.getWidth() + 7) / 8; // n\u00famero de bytes
        int iHeight = centeredimage.getHeight();

        byte[] bData = new byte[getImageHeader().length + 4 + iWidth * iHeight];

        System.arraycopy(getImageHeader(), 0, bData, 0, getImageHeader().length);

        int index = getImageHeader().length;

        bData[index++] = (byte) (iWidth % 256);
        bData[index++] = (byte) (iWidth / 256);
        bData[index++] = (byte) (iHeight % 256);
        bData[index++] = (byte) (iHeight / 256);

        int p;
        for (int i = 0; i < centeredimage.getHeight(); i++) {
            for (int j = 0; j < centeredimage.getWidth(); j = j + 8) {
                p = 0x00;
                for (int d = 0; d < 8; d++) {
                    p = p << 1;
                    if (centeredimage.isBlack(j + d, i)) {
                        p = p | 0x01;
                    }
                }

                bData[index++] = (byte) p;
            }
        }
        return bData;
    }

    protected class CenteredImage {

        private final BufferedImage image;
        private final int width;

        public CenteredImage(BufferedImage image, int width) {
            this.image = image;
            this.width = width;
        }

        public int getHeight() {
            return image.getHeight();
        }

        public int getWidth() {
            return width;
        }

        public boolean isBlack(int x, int y) {

            int centeredx = x + (image.getWidth() - width) / 2;
            if (centeredx < 0 || centeredx >= image.getWidth() || y < 0 || y >= image.getHeight()) {
                return false;
            } else {
                int rgb = image.getRGB(centeredx, y);

                int gray = (int) (0.30 * ((rgb >> 16) & 0xff)
                        + 0.59 * ((rgb >> 8) & 0xff)
                        + 0.11 * (rgb & 0xff));

                return gray < 128;
            }
        }
    }
}
