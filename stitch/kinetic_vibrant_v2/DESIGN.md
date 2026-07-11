---
name: Kinetic Vibrant (V2)
colors:
  surface: '#f9f9fc'
  surface-dim: '#dadadc'
  surface-bright: '#f9f9fc'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f3f3f6'
  surface-container: '#eeeef0'
  surface-container-high: '#e8e8ea'
  surface-container-highest: '#e2e2e5'
  on-surface: '#1a1c1e'
  on-surface-variant: '#594139'
  inverse-surface: '#2f3133'
  inverse-on-surface: '#f0f0f3'
  outline: '#8d7168'
  outline-variant: '#e1bfb5'
  surface-tint: '#ab3500'
  primary: '#ab3500'
  on-primary: '#ffffff'
  primary-container: '#ff6b35'
  on-primary-container: '#5f1900'
  inverse-primary: '#ffb59d'
  secondary: '#00687b'
  on-secondary: '#ffffff'
  secondary-container: '#6be1ff'
  on-secondary-container: '#006375'
  tertiary: '#3b6a00'
  on-tertiary: '#ffffff'
  tertiary-container: '#68ab1d'
  on-tertiary-container: '#1d3900'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#ffdbd0'
  primary-fixed-dim: '#ffb59d'
  on-primary-fixed: '#390c00'
  on-primary-fixed-variant: '#832600'
  secondary-fixed: '#adecff'
  secondary-fixed-dim: '#5ed5f3'
  on-secondary-fixed: '#001f26'
  on-secondary-fixed-variant: '#004e5d'
  tertiary-fixed: '#aef764'
  tertiary-fixed-dim: '#93da4b'
  on-tertiary-fixed: '#0e2000'
  on-tertiary-fixed-variant: '#2b5000'
  background: '#f9f9fc'
  on-background: '#1a1c1e'
  surface-variant: '#e2e2e5'
typography:
  display-xl:
    fontFamily: Sora
    fontSize: 48px
    fontWeight: '800'
    lineHeight: 56px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Sora
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.01em
  headline-lg-mobile:
    fontFamily: Sora
    fontSize: 28px
    fontWeight: '700'
    lineHeight: 36px
  body-md:
    fontFamily: Hanken Grotesk
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  label-sm:
    fontFamily: JetBrains Mono
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.05em
rounded:
  sm: 0.5rem
  DEFAULT: 1rem
  md: 1.5rem
  lg: 2rem
  xl: 3rem
  full: 9999px
spacing:
  unit: 4px
  gutter: 16px
  margin-mobile: 20px
  margin-desktop: 48px
  glass-padding: 24px
---

## Brand & Style

The design system is built for a hyper-modern, youthful audience that values speed and expressive technology. It merges **Glassmorphism** with **Kinetic Minimalism** to create a UI that feels both physically tangible and digitally fluid. 

The personality is professional yet playful—handling financial data with precision while maintaining a high-energy, "fast" aesthetic. The visual mood is defined by depth, utilizing translucent layers (frosted glass) and vibrant accents to guide the eye toward the primary voice-first interaction. It aims for a "z-index" feel, where elements appear to float in a defined 3D space.

## Colors

The palette centers on an **Energetic Orange** (#ff6b35) to drive action and focus. This is supported by a range of functional pastels (Soft Cyan and Mint) used for category differentiation and glass-layered backgrounds.

- **Primary:** High-energy orange for the hero voice interface and main CTAs.
- **Surface:** Uses ultra-soft neutrals (#f8f9fa) and varying opacities of white (10-40%) to achieve the glassmorphic frosted effect.
- **Accents:** High-contrast dark accents (#1a1c1e) are reserved for typography and deep shadows to ensure the light, airy glass elements remain readable and anchored.

## Typography

This design system utilizes a high-contrast typographic hierarchy. **Sora** provides a bold, geometric presence for "Oversized Display" headings, capturing the youthful and futuristic vibe. 

**Hanken Grotesk** is used for body text and financial data, offering exceptional clarity and a contemporary feel. For secondary data points, such as timestamps or currency metadata, **JetBrains Mono** adds a technical, "ledger-like" precision that reinforces the accounting nature of the app.

## Layout & Spacing

The layout follows a **Fluid Grid** model with generous safe areas to support the floating component style. Elements are spaced using a 4px baseline rhythm, but the overall feel remains "airy" with large internal paddings (24px+) within containers.

- **Mobile:** 4-column grid with 20px side margins. The hero voice element is fixed to the bottom-center "thumb zone."
- **Desktop/Tablet:** 12-column grid. Content is centered in a max-width container (1200px) to maintain focus.
- **Z-Axis Spacing:** Use vertical translations (8px-16px) during transitions to emphasize the kinetic, layered nature of the UI.

## Elevation & Depth

Hierarchy is established through **Backdrop Blurs** and **Ambient Shadows**. 

1.  **Level 0 (Base):** Subtle pastel gradients or off-white.
2.  **Level 1 (Glass Cards):** White at 40% opacity with a 20px backdrop blur and a thin, 1px semi-transparent border to define edges.
3.  **Level 2 (Active Elements):** High-opacity glass with extra-diffused, orange-tinted shadows (0px 20px 40px rgba(255, 107, 53, 0.15)) to create the "z-index" lift.
4.  **Level 3 (Voice Hero):** The primary interaction point uses the highest elevation, featuring a glowing aura to indicate voice-activity state.

## Shapes

The shape language is defined by **Ultra-Large Roundedness** (Pill-shaped/Round 12+). This "squishy" geometry softens the professional nature of accounting and makes the app feel approachable. 

Primary cards and containers use a minimum radius of 24px (`rounded-xl`). The Voice Hero element is always a perfect circle or a pill-shaped button to distinguish it from static informational cards.

## Components

### Voice Hero (The Core)
This is not a standard button. It is a large, circular floating action element. It uses a primary orange gradient and, when active, generates "Kinetic Ripples"—glassmorphic rings that pulse outward to represent voice capture.

### Glass Cards
Containers for financial summaries. They must have a `backdrop-filter: blur(20px)` and a soft 1px border (`rgba(255,255,255,0.5)`). Corners are always `rounded-xl`.

### Inputs & Fields
Input fields are simplified to a single line or a very subtle glass inset. When focused, the label should transform using a kinetic "pop" animation.

### Chips & Tags
Used for transaction categories. These should use the secondary and tertiary pastel colors with high-contrast dark text, shaped as full pills.

### Floating Lists
List items should appear as separate glass strips rather than a single block, with 8px of vertical spacing between them to maintain the "layered" feel.