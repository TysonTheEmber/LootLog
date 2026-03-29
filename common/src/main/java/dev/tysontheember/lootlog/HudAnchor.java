package dev.tysontheember.lootlog;

/**
 * Screen corner anchor for the pickup HUD.
 * Computes entry positions based on screen dimensions and config offsets.
 */
public enum HudAnchor {
    TOP_LEFT {
        @Override
        public int anchorX(int screenWidth, int entryWidth, int offset) {
            return offset;
        }

        @Override
        public int anchorY(int screenHeight, int entryIndex, int entryHeight, int spacing, int offset) {
            return offset + entryIndex * (entryHeight + spacing);
        }
    },
    TOP_RIGHT {
        @Override
        public int anchorX(int screenWidth, int entryWidth, int offset) {
            return screenWidth - entryWidth - offset;
        }

        @Override
        public int anchorY(int screenHeight, int entryIndex, int entryHeight, int spacing, int offset) {
            return offset + entryIndex * (entryHeight + spacing);
        }
    },
    BOTTOM_LEFT {
        @Override
        public int anchorX(int screenWidth, int entryWidth, int offset) {
            return offset;
        }

        @Override
        public int anchorY(int screenHeight, int entryIndex, int entryHeight, int spacing, int offset) {
            return screenHeight - offset - (entryIndex + 1) * (entryHeight + spacing) + spacing;
        }
    },
    BOTTOM_RIGHT {
        @Override
        public int anchorX(int screenWidth, int entryWidth, int offset) {
            return screenWidth - entryWidth - offset;
        }

        @Override
        public int anchorY(int screenHeight, int entryIndex, int entryHeight, int spacing, int offset) {
            return screenHeight - offset - (entryIndex + 1) * (entryHeight + spacing) + spacing;
        }
    };

    public abstract int anchorX(int screenWidth, int entryWidth, int offset);

    /**
     * Default Y positioning. TOP anchors grow downward, BOTTOM anchors grow upward.
     */
    public abstract int anchorY(int screenHeight, int entryIndex, int entryHeight, int spacing, int offset);

    /**
     * Y positioning with growth direction override.
     * When growth direction opposes the anchor's natural direction, the layout is flipped.
     */
    public int anchorY(int screenHeight, int entryIndex, int entryHeight, int spacing,
                       int offset, GrowthDirection growth) {
        if (growth == GrowthDirection.NORMAL) {
            // Natural direction: bottom anchors grow up, top anchors grow down
            return anchorY(screenHeight, entryIndex, entryHeight, spacing, offset);
        }

        // INVERSE: flip the natural direction
        boolean naturallyGrowsDown = (this == TOP_LEFT || this == TOP_RIGHT);
        if (naturallyGrowsDown) {
            // TOP anchor but growth UP: newest at top, older entries go up (above anchor)
            return offset - entryIndex * (entryHeight + spacing);
        } else {
            // BOTTOM anchor but growth DOWN: newest at bottom, older entries go down
            return screenHeight - offset + entryIndex * (entryHeight + spacing) - entryHeight;
        }
    }
}
