package dev.tysontheember.lootlog;

/**
 * Abstraction for text effect rendering.
 * When EmbersTextAPI is available, the platform provides an implementation
 * that parses markup and renders styled text.
 * When unavailable, {@link DefaultTextEffectBridge} strips markup and renders plain text.
 */
public interface TextEffectBridge {

    /**
     * Render text with optional markup effects.
     *
     * @param bridge    the RenderBridge for actual draw calls
     * @param markup    markup string ({@code {name}} token already replaced)
     * @param x         render X position
     * @param y         render Y position
     * @param baseColor base ARGB color (used when markup doesn't override color)
     * @param shadow    whether to draw text shadow
     * @param timeMs    animation time for time-based effects
     * @return the rendered text width in pixels
     */
    int renderMarkupText(RenderBridge bridge, String markup, int x, int y,
                         int baseColor, boolean shadow, long timeMs);

    /**
     * Get the pixel width of the text after stripping markup tags.
     */
    int getMarkupTextWidth(RenderBridge bridge, String markup);

    /**
     * Strip markup tags to get plain text.
     */
    String stripMarkup(String markup);
}
