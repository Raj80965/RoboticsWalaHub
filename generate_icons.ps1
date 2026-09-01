Add-Type -AssemblyName System.Drawing

$src = "C:\Users\RAJ RAJGURU\.gemini\antigravity-ide\brain\0872a164-2ddb-421e-964a-fc8920fc666f\.user_uploaded\media_1788274272683.jpg"
$img = [System.Drawing.Image]::FromFile($src)

$resDir = "d:\Project\RoboticsWalaHub-main\app\src\main\res"

# Save full quality drawable
$drawableDir = "$resDir\drawable"
if (!(Test-Path $drawableDir)) { New-Item -ItemType Directory -Path $drawableDir -Force }
$img.Save("$drawableDir\app_logo.png", [System.Drawing.Imaging.ImageFormat]::Png)
$img.Save("$drawableDir\ic_launcher.png", [System.Drawing.Imaging.ImageFormat]::Png)
$img.Save("$drawableDir\ic_launcher_round.png", [System.Drawing.Imaging.ImageFormat]::Png)

# Generate Mipmap densities
$sizes = @{
    "mipmap-mdpi" = 48
    "mipmap-hdpi" = 72
    "mipmap-xhdpi" = 96
    "mipmap-xxhdpi" = 144
    "mipmap-xxxhdpi" = 192
}

foreach ($folder in $sizes.Keys) {
    $targetDir = "$resDir\$folder"
    if (!(Test-Path $targetDir)) { New-Item -ItemType Directory -Path $targetDir -Force }
    $dim = $sizes[$folder]
    $bmp = New-Object System.Drawing.Bitmap($dim, $dim)
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
    $g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    $g.DrawImage($img, 0, 0, $dim, $dim)
    $bmp.Save("$targetDir\ic_launcher.png", [System.Drawing.Imaging.ImageFormat]::Png)
    $bmp.Save("$targetDir\ic_launcher_round.png", [System.Drawing.Imaging.ImageFormat]::Png)
    $g.Dispose()
    $bmp.Dispose()
}

$img.Dispose()
Write-Host "Icons and drawables generated successfully!"

