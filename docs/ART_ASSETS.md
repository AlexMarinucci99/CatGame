# Running cat asset

`src/main/resources/images/player/cat-run-v1.png` is a generated 1536×1024 RGBA atlas with six 512×512 cells, ordered left to right, then top to bottom. It was created with the built-in imagegen tool, using the existing `cat.png` as the identity reference. The original file remains unchanged.

`PlayerAnimation` selects source cells at runtime and aligns their foot baselines. It does not alter the collision box. The original still image is used when idle in the boss encounter and in reduced-effects mode. The normal-level conveyor is drawn directly in JavaFX Canvas and does not use generated artwork.

## Generation prompt

Create a production-ready 2D game sprite sheet using the attached character as the exact identity reference. The character is the same stocky muscular dark charcoal cat with cream belly/muzzle, stern face, gold M on forehead, cigar and knife. Preserve outfit-free anatomy, colors, face and illustration style. A seamless six-frame RUN-IN-PLACE cycle facing right in the same three-quarter side view. Legs and arms visibly change pose across all six frames, with a heavy believable running gait, alternating contact/passing/flight poses. The knife stays safely in the same right hand, cigar remains in mouth. Consistent camera, lighting, head size, body proportions and scale across frames. Clean neutral non-glowing rendering. EXACT layout: 3 columns by 2 rows, six equally sized square cells, left-to-right then top-to-bottom, on a truly transparent background. Each cell contains one complete cat with generous transparent padding; no parts cross cell boundaries. All six sprites have the same center and same foot baseline within their cell; no camera movement or horizontal displacement. Keep head and torso stable enough to avoid jitter. No floor, no shadows below feet, no text, no numbers, no grid lines, no frames, no UI. Output a 3:2 landscape PNG sprite atlas with alpha, ideally 1536x1024.

## Cleanup prompt

Edit this six-frame game sprite sheet only to make it clean for runtime compositing. Preserve exactly the six cat poses, character identity, cell layout, colors, size and illustration detail. Remove ALL brown background haze, glow, vignette, floor shadows, diffuse aura, and every pixel outside each cat silhouette. The area around each cat and between all cells must be genuinely alpha-zero transparent with clean antialiased cutout edges. The cat interiors must be fully opaque. No halo or background color around fur. No added content, labels, text, grid, borders, or lighting effects. Keep the 3-column by 2-row layout and 1536x1024 dimensions.

# Energy combat assets

Generated with the built-in imagegen tool; original files remain available as fallbacks.

- `images/effects/energy-impact-v1.png`: 4 columns × 2 rows. Top row loops energy flight; bottom row plays impact/debris once. Runtime playback uses the complete cells and fades impacts over their existing lifetime.
- `images/blocks/parcels-v2.png`: two columns showing a single cardboard delivery box and a stacked pair, with smile branding, black packing tape and shipping labels. Alpha bounds are computed once when loaded, then aligned to the conveyor.
- The special parcel reuses the box art with a red MC badge drawn in Canvas.

Energy prompt: Production game VFX sprite atlas for a dark illustrated 2D action game. Exactly 8 cells in a 4-column by 2-row grid. Truly transparent alpha background, no floor, labels or grid lines. Top row four consecutive frames of a compact golden amber energy orb flying right, cream core, restrained warm flame contours and short trailing strokes. Bottom row four stages of an orb striking and destroying a cardboard delivery parcel: contact spark and fragments, expanding impact, dissipating fragments, sparse fading embers/cardboard. No intact box in bottom row. Fixed center, generous transparent cell padding, professional hand-painted style, readable at 40–90 pixels, glow confined to the effect.

Parcel prompt: Game prop sprite atlas showing only ordinary cardboard delivery packages. Exactly two equal square cells side by side, transparent alpha. Cell 1 one closed Amazon delivery box; cell 2 two matching boxes stacked to the same total height. Kraft corrugated cardboard, black smile logo on front, black branded tape and shipping label. Consistent three-quarter view, polished hand-painted warehouse illustration, crisp at 50 pixels. Centered cells with transparent padding and the same bottom baseline. No ground, cast shadows, glow, borders or extra items.
