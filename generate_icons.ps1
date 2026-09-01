Add-Type -AssemblyName System.Drawing

$srcPath = "C:\Users\RAJ RAJGURU\.gemini\antigravity-ide\brain\0872a164-2ddb-421e-964a-fc8920fc666f\.user_uploaded\media_1788275597372.jpg"
$src = [System.Drawing.Bitmap]::FromFile($srcPath)

$resDir = "d:\Project\RoboticsWalaHub-main\app\src\main\res"
$dimMaster = $src.Width

# Create 32-bit ARGB image with high quality circular clip
$masterBmp = New-Object System.Drawing.Bitmap($dimMaster, $dimMaster, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
$g = [System.Drawing.Graphics]::FromImage($masterBmp)
$g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
$g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
$g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
$g.Clear([System.Drawing.Color]::Transparent)

# Smooth circular clip path
$path = New-Object System.Drawing.Drawing2D.GraphicsPath
$path.AddEllipse(2, 2, $dimMaster - 4, $dimMaster - 4)
$g.SetClip($path)

$g.DrawImage($src, 0, 0, $dimMaster, $dimMaster)
$g.Dispose()
$src.Dispose()

# Save drawable master icons
$drawableDir = "$resDir\drawable"
if (!(Test-Path $drawableDir)) { New-Item -ItemType Directory -Path $drawableDir -Force }
$masterBmp.Save("$drawableDir\app_logo.png", [System.Drawing.Imaging.ImageFormat]::Png)
$masterBmp.Save("$drawableDir\ic_launcher.png", [System.Drawing.Imaging.ImageFormat]::Png)
$masterBmp.Save("$drawableDir\ic_launcher_round.png", [System.Drawing.Imaging.ImageFormat]::Png)

# Generate densities for all launcher mipmap sizes
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
    $size = $sizes[$folder]
    $bmp = New-Object System.Drawing.Bitmap($size, $size, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $gSub = [System.Drawing.Graphics]::FromImage($bmp)
    $gSub.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
    $gSub.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $gSub.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    $gSub.Clear([System.Drawing.Color]::Transparent)
    
    $pathSub = New-Object System.Drawing.Drawing2D.GraphicsPath
    $pathSub.AddEllipse(1, 1, $size - 2, $size - 2)
    $gSub.SetClip($pathSub)
    
    $gSub.DrawImage($masterBmp, 0, 0, $size, $size)
    $bmp.Save("$targetDir\ic_launcher.png", [System.Drawing.Imaging.ImageFormat]::Png)
    $bmp.Save("$targetDir\ic_launcher_round.png", [System.Drawing.Imaging.ImageFormat]::Png)
    $gSub.Dispose()
    $bmp.Dispose()
}

$masterBmp.Dispose()
Write-Host "New pristine clean high-res circular icon generated successfully!"



