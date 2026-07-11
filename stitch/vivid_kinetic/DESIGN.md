---
name: Vivid Kinetic
colors:
  surface: '#131313'
  surface-dim: '#131313'
  surface-bright: '#393939'
  surface-container-lowest: '#0e0e0e'
  surface-container-low: '#1c1b1b'
  surface-container: '#201f1f'
  surface-container-high: '#2a2a2a'
  surface-container-highest: '#353534'
  on-surface: '#e5e2e1'
  on-surface-variant: '#e1bfb5'
  inverse-surface: '#e5e2e1'
  inverse-on-surface: '#313030'
  outline: '#a98a80'
  outline-variant: '#594139'
  surface-tint: '#ffb59d'
  primary: '#ffb59d'
  on-primary: '#5d1900'
  primary-container: '#ff6b35'
  on-primary-container: '#5f1900'
  inverse-primary: '#ab3500'
  secondary: '#c7fff0'
  on-secondary: '#00382f'
  secondary-container: '#00f2d1'
  on-secondary-container: '#006a5a'
  tertiary: '#deb7ff'
  on-tertiary: '#4a007f'
  tertiary-container: '#bf79ff'
  on-tertiary-container: '#4b0081'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#ffdbd0'
  primary-fixed-dim: '#ffb59d'
  on-primary-fixed: '#390c00'
  on-primary-fixed-variant: '#832600'
  secondary-fixed: '#26fedc'
  secondary-fixed-dim: '#00dfc1'
  on-secondary-fixed: '#00201a'
  on-secondary-fixed-variant: '#005144'
  tertiary-fixed: '#f1dbff'
  tertiary-fixed-dim: '#deb7ff'
  on-tertiary-fixed: '#2d0050'
  on-tertiary-fixed-variant: '#680eac'
  background: '#131313'
  on-background: '#e5e2e1'
  surface-variant: '#353534'
typography:
  display-lg:
    fontFamily: Sora
    fontSize: 48px
    fontWeight: '800'
    lineHeight: '1.1'
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Sora
    fontSize: 32px
    fontWeight: '700'
    lineHeight: '1.2'
  headline-lg-mobile:
    fontFamily: Sora
    fontSize: 28px
    fontWeight: '700'
    lineHeight: '1.2'
  body-md:
    fontFamily: Hanken Grotesk
    fontSize: 16px
    fontWeight: '400'
    lineHeight: '1.6'
  finance-xl:
    fontFamily: Sora
    fontSize: 40px
    fontWeight: '600'
    lineHeight: '1'
    letterSpacing: -0.04em
  label-caps:
    fontFamily: JetBrains Mono
    fontSize: 12px
    fontWeight: '500'
    lineHeight: '1'
    letterSpacing: 0.1em
rounded:
  sm: 0.5rem
  DEFAULT: 1rem
  md: 1.5rem
  lg: 2rem
  xl: 3rem
  full: 9999px
spacing:
  unit: 8px
  container-padding: 24px
  gutter: 16px
  section-gap: 40px
---

## Brand & Style
The design system is engineered for a youthful, high-energy demographic navigating the intersection of finance and voice technology. It moves away from the stuffy, rigid structures of traditional accounting, instead embracing a **Dynamic Minimalism** infused with **Glassmorphism** and **Vaporwave-inspired depth**.

The personality is proactive, optimistic, and fast. The UI should feel like a living organism—responsive to the user's voice with fluid transitions, soft glows, and floating layers that suggest weightlessness. We prioritize an "Airy" aesthetic where whitespace acts as a structural element rather than a void.

## Colors
The palette is anchored by a high-saturation **Electric Orange** (#ff6b35) for primary actions and financial growth indicators. To contrast this heat, we use a **Neon Teal** secondary color for balance and success states.

The background is a deep, obsidian neutral to allow for vibrant mesh gradients and glassmorphic blurs to pop. Surfaces are not solid; they are semi-transparent tints of the background or deep violets, creating a sense of infinite depth. Use "Glow" states instead of traditional active states, where the primary color bleeds into the surrounding area via a diffused drop shadow.

## Typography
Typography is a core visual driver. **Sora** provides a high-tech, geometric foundation for headlines and critical financial figures. Numeric data should be rendered with generous sizing and tight letter spacing to feel impactful.

**Hanken Grotesk** handles body copy with professional clarity, while **JetBrains Mono** is reserved for metadata, transaction IDs, and secondary labels to lean into the "digital ledger" aspect of accounting. All headers should use an optical "tight" tracking to maintain the energetic, compressed feel of the brand.

## Layout & Spacing
This design system utilizes an **Asymmetric Fluid Grid**. Avoid rigid symmetry; instead, use staggered card heights and off-center alignments to create visual interest. 

The spacing rhythm is built on an 8px base unit. Components should have "Breathing Room"—internal padding is intentionally generous (minimum 24px for cards) to prevent the UI from feeling cramped. For mobile, use a 24px side margin to ensure content feels floated within the frame.

## Elevation & Depth
Depth is communicated through **Optical Stacking** rather than traditional shadows. 
1.  **Base:** Deep neutral background with a subtle, animated mesh gradient in the corners (Teal/Violet).
2.  **Floating Surface:** Glassmorphic containers with a `backdrop-filter: blur(20px)` and a 1px semi-transparent white border (0.1 opacity).
3.  **Active Elements:** Primary buttons use a 20px diffused "Glow" shadow utilizing the button's own hex color at 40% opacity.
4.  **Voice Overlay:** When active, a full-screen blurred overlay creates a focused "Voice Space," dimming the background content.

## Shapes
The shape language is dominated by **Hyper-Radii**. All containers, buttons, and input fields utilize "Pill" styling or extremely large corner radii to feel soft and approachable. 

Interactive icons should be encased in circular glass containers. Avoid sharp 90-degree angles entirely; even the smallest decorative elements should have at least a 4px radius to maintain the "Soft Tech" aesthetic.

## Components

### Floating Tab Bar
The navigation is a detached, floating capsule. The center features an oversized, circular **Voice Action Button** that breaks the top plane of the bar. This button uses the Primary Orange with a pulse animation during voice input.

### Voice-Responsive Cards
Financial cards should be glassmorphic. When the user speaks about a specific category (e.g., "How much did I spend on food?"), the relevant card should scale slightly (1.05x) and gain a vibrant outer glow.

### Buttons & Inputs
*   **Primary Action:** Pill-shaped, Primary Orange, with white bold text.
*   **Secondary Action:** Ghost style with a thick 2px border and high-blur backdrop.
*   **Input Fields:** Minimalist lines or soft-filled capsules. Labels should float above the field in the `label-caps` style.

### Data Visualization
Charts should avoid thin lines. Use thick, rounded-cap strokes and soft gradients under the trend lines. Data points should be large, glowing "Orbs."