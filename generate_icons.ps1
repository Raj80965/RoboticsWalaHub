Add-Type -AssemblyName System.Drawing

$srcPath = "C:\Users\RAJ RAJGURU\.gemini\antigravity-ide\brain\0872a164-2ddb-421e-964a-fc8920fc666f\.user_uploaded\media_1788274272683.jpg"
$src = [System.Drawing.Bitmap]::FromFile($srcPath)

$resDir = "d:\Project\RoboticsWalaHub-main\app\src\main\res"

# Medal center & radius for tight circular crop
$cx = 512
$cy = 441
$radius = 392
$diameter = $radius * 2

$cropped = New-Object System.Drawing.Bitmap($diameter, $diameter, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
$g = [System.Drawing.Graphics]::FromImage($cropped)
$g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
$g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
$g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
$g.Clear([System.Drawing.Color]::Transparent)

$path = New-Object System.Drawing.Drawing2D.GraphicsPath
$path.AddEllipse(0, 0, $diameter, $diameter)
$g.SetClip($path)

$g.DrawImage($src, -$cx + $radius, -$cy + $radius, $src.Width, $src.Height)
$g.Dispose()
$src.Dispose()

# Save full resolution cropped circular drawables
$drawableDir = "$resDir\drawable"
if (!(Test-Path $drawableDir)) { New-Item -ItemType Directory -Path $drawableDir -Force }
$cropped.Save("$drawableDir\app_logo.png", [System.Drawing.Imaging.ImageFormat]::Png)
$cropped.Save("$drawableDir\ic_launcher.png", [System.Drawing.Imaging.ImageFormat]::Png)
$cropped.Save("$drawableDir\ic_launcher_round.png", [System.Drawing.Imaging.ImageFormat]::Png)

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
    $bmp = New-Object System.Drawing.Bitmap($dim, $dim, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $gSub = [System.Drawing.Graphics]::FromImage($bmp)
    $gSub.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
    $gSub.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $gSub.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    $gSub.Clear([System.Drawing.Color]::Transparent)
    
    $pathSub = New-Object System.Drawing.Drawing2D.GraphicsPath
    $pathSub.AddEllipse(0, 0, $dim, $dim)
    $gSub.SetClip($pathSub)
    
    $gSub.DrawImage($cropped, 0, 0, $dim, $dim)
    $bmp.Save("$targetDir\ic_launcher.png", [System.Drawing.Imaging.ImageFormat]::Png)
    $bmp.Save("$targetDir\ic_launcher_round.png", [System.Drawing.Imaging.ImageFormat]::Png)
    $gSub.Dispose()
    $bmp.Dispose()
}

$cropped.Dispose()
Write-Host "Circular transparent medal crop generated successfully!"


