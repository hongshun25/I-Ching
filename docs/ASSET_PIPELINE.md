# Asset Pipeline

更新日期：2026-06-21

## Policy

Production assets must be committed to the repository. Gradle builds must not download UI assets at build time.

Allowed sources:

- SIL Open Font License 1.1 fonts.
- Apache-2.0 Material icons.
- CC0 / Public Domain images and textures.
- Project-authored vectors/placeholders.
- Project-owned Stitch-assisted artwork that is generated or authored for this repository, committed under `res/`, and documented with transform notes/checksums.

Disallowed without separate written verification:

- Stitch-exported `lh3.googleusercontent.com/aida-public/...` images.
- Stock images or icons without clear commercial redistribution terms.
- Runtime network-loaded production UI assets.

Every production font/icon/image/texture must be listed in `tools/assets/asset_manifest.json` with:

- `name`
- `type`
- `sourceUrl`
- `license`
- `targetPath`
- `transform`
- `checksumSha256`

`AssetManifestTest` verifies target files exist and match manifest checksums.

## Current Assets

- Fonts:
  - `app/src/main/res/font/noto_sans_tc.ttf`
  - `app/src/main/res/font/noto_serif_cjk_tc_regular.otf`
- Textures:
  - `app/src/main/res/drawable-nodpi/bg_paper_texture_light.webp`
  - `app/src/main/res/drawable-nodpi/bg_paper_texture_dark.webp`
- Artwork:
  - `app/src/main/res/drawable-nodpi/art_scholar_waterfall.webp`
  - `app/src/main/res/drawable-nodpi/art_onboarding_daily.webp`
  - `app/src/main/res/drawable-nodpi/art_onboarding_question.webp`
  - `app/src/main/res/drawable-nodpi/art_onboarding_records.webp`
- Icons:
  - `app/src/main/res/drawable/ic_*.xml`

## Reproduction Notes

The current assets were staged outside the repo, optimized, then copied into `res/`.

Fonts were copied unchanged from upstream:

```bash
curl -L --fail -o /tmp/iching_assets/noto_sans_tc.ttf \
  https://raw.githubusercontent.com/google/fonts/main/ofl/notosanstc/NotoSansTC%5Bwght%5D.ttf

curl -L --fail -o /tmp/iching_assets/noto_serif_cjk_tc_regular.otf \
  https://raw.githubusercontent.com/notofonts/noto-cjk/main/Serif/OTF/TraditionalChinese/NotoSerifCJKtc-Regular.otf
```

Paper texture was extracted from ambientCG `Paper001_1K-JPG.zip`:

```bash
curl -L --fail -o /tmp/iching_assets/Paper001_1K-JPG.zip \
  'https://ambientCG.com/get?file=Paper001_1K-JPG.zip'

unzip -o /tmp/iching_assets/Paper001_1K-JPG.zip \
  Paper001_1K-JPG_Color.jpg -d /tmp/iching_assets

convert /tmp/iching_assets/Paper001_1K-JPG_Color.jpg \
  -resize 720x720^ -gravity center -extent 720x720 -quality 72 \
  app/src/main/res/drawable-nodpi/bg_paper_texture_light.webp

convert /tmp/iching_assets/Paper001_1K-JPG_Color.jpg \
  -resize 720x720^ -gravity center -extent 720x720 \
  -modulate 70,80,100 -fill '#111111' -colorize 24% -quality 72 \
  app/src/main/res/drawable-nodpi/bg_paper_texture_dark.webp
```

The Met artwork was converted from the web-large public-domain image:

```bash
curl -L --fail -o /tmp/iching_assets/met_scholar_waterfall.jpg \
  https://images.metmuseum.org/CRDImages/as/web-large/DP154090.jpg

convert /tmp/iching_assets/met_scholar_waterfall.jpg \
  -resize 900x900^ -gravity center -extent 900x900 -quality 78 \
  app/src/main/res/drawable-nodpi/art_scholar_waterfall.webp
```

Onboarding ink-wash artwork was generated locally as project-owned Stitch-assisted visual material:

```bash
convert -size 640x640 xc:none -fill 'rgba(166,124,55,0.10)' \
  -draw 'circle 320,320 320,48' \
  -fill 'rgba(26,26,26,0.07)' -draw 'circle 318,320 318,126' \
  -quality 82 app/src/main/res/drawable-nodpi/art_onboarding_daily.webp
```

The question and records variants use the same procedural approach with different line/card motifs.

After changing any target file, update the manifest checksum:

```bash
sha256sum path/to/asset
```

Then run:

```bash
./gradlew testDebugUnitTest
```

## Adding A New Asset

1. Verify the source license before downloading.
2. Save or generate the asset outside `res/`.
3. Optimize only the committed output needed by the app.
4. Add the output under the appropriate Android resource directory.
5. Add or update `tools/assets/asset_manifest.json`.
6. Update `docs/ASSET_LICENSES.md` if this introduces a new source or license.
7. Run `./gradlew testDebugUnitTest lintDebug`.
