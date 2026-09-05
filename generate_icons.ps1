Add-Type -AssemblyName System.Drawing

$srcPath = "d:\Project\RoboticsWalaHub-main\app\src\main\res\drawable\app_logo.png"
$src = [System.Drawing.Bitmap]::FromFile($srcPath)
$resDir = "d:\Project\RoboticsWalaHub-main\app\src\main\res"

# Densities configuration:
# Legacy icons: 48, 72, 96, 144, 192
# Adaptive foreground (108dp base): 108, 162, 216, 324, 432
$densities = @(
    @{ folder = "mipmap-mdpi"; legacy = 48; adaptive = 108 },
    @{ folder = "mipmap-hdpi"; legacy = 72; adaptive = 162 },
    @{ folder = "mipmap-xhdpi"; legacy = 96; adaptive = 216 },
    @{ folder = "mipmap-xxhdpi"; legacy = 144; adaptive = 324 },
    @{ folder = "mipmap-xxxhdpi"; legacy = 192; adaptive = 432 }
)

$bgColor = [System.Drawing.Color]::FromArgb(255, 0, 0, 0)

foreach ($d in $densities) {
    $dir = Join-Path $resDir $d.folder
    if (!(Test-Path $dir)) { New-Item -ItemType Directory -Path $dir -Force }

    # 1. Generate Adaptive Foreground (108dp base)
    $adSize = $d.adaptive
    $adBmp = New-Object System.Drawing.Bitmap($adSize, $adSize, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $gAd = [System.Drawing.Graphics]::FromImage($adBmp)
    $gAd.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
    $gAd.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $gAd.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    $gAd.Clear($bgColor)

    $contentSize = [int][Math]::Round($adSize * (254.0 / 432.0))
    $contentOffset = [int][Math]::Round(($adSize - $contentSize) / 2)
    $destAd = New-Object System.Drawing.Rectangle($contentOffset, $contentOffset, $contentSize, $contentSize)
    $gAd.DrawImage($src, $destAd, 0, 0, $src.Width, $src.Height, [System.Drawing.GraphicsUnit]::Pixel)
    $gAd.Dispose()

    $adBmp.Save((Join-Path $dir "ic_launcher_foreground.png"), [System.Drawing.Imaging.ImageFormat]::Png)
    $adBmp.Dispose()

    # 2. Generate Legacy Square / Squircle Icon (ic_launcher.png) - 100% full-bleed
    $legSize = $d.legacy
    $legBmp = New-Object System.Drawing.Bitmap($legSize, $legSize, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $gLeg = [System.Drawing.Graphics]::FromImage($legBmp)
    $gLeg.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
    $gLeg.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $gLeg.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    $gLeg.Clear([System.Drawing.Color]::Transparent)
    $gLeg.DrawImage($src, 0, 0, $legSize, $legSize)
    $gLeg.Dispose()

    $legBmp.Save((Join-Path $dir "ic_launcher.png"), [System.Drawing.Imaging.ImageFormat]::Png)
    $legBmp.Dispose()

    # 3. Generate Legacy Round Icon (ic_launcher_round.png)
    $rndBmp = New-Object System.Drawing.Bitmap($legSize, $legSize, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $gRnd = [System.Drawing.Graphics]::FromImage($rndBmp)
    $gRnd.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
    $gRnd.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $gRnd.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    $gRnd.Clear([System.Drawing.Color]::Transparent)

    $rndPath = New-Object System.Drawing.Drawing2D.GraphicsPath
    $rndPath.AddEllipse(0, 0, $legSize, $legSize)
    $gRnd.SetClip($rndPath)
    $gRnd.Clear($bgColor)

    $rndContentSize = [int][Math]::Round($legSize * 0.88)
    $rndContentOffset = [int][Math]::Round(($legSize - $rndContentSize) / 2)
    $destRnd = New-Object System.Drawing.Rectangle($rndContentOffset, $rndContentOffset, $rndContentSize, $rndContentSize)
    $gRnd.DrawImage($src, $destRnd, 0, 0, $src.Width, $src.Height, [System.Drawing.GraphicsUnit]::Pixel)
    $gRnd.Dispose()

    $rndBmp.Save((Join-Path $dir "ic_launcher_round.png"), [System.Drawing.Imaging.ImageFormat]::Png)
    $rndBmp.Dispose()
}

$src.Dispose()

# Create mipmap-anydpi-v26 adaptive icon XMLs
$anydpiDir = "$resDir\mipmap-anydpi-v26"
if (!(Test-Path $anydpiDir)) { New-Item -ItemType Directory -Path $anydpiDir -Force }

$adaptiveXml = @"
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/black" />
    <foreground android:drawable="@mipmap/ic_launcher_foreground" />
</adaptive-icon>
"@

$adaptiveXml | Out-File -FilePath "$anydpiDir\ic_launcher.xml" -Encoding utf8
$adaptiveXml | Out-File -FilePath "$anydpiDir\ic_launcher_round.xml" -Encoding utf8

Write-Host "Accurate icons generated successfully!"
