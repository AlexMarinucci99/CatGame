$ErrorActionPreference = 'Stop'
$RootDir = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
Set-Location $RootDir

$AppName = 'CatGame'
$MainClass = 'com.master.bro.catdinogame.GameLauncher'
$AppVersion = if ($env:APP_VERSION) { $env:APP_VERSION } else { '1.0.0' }
if ($AppVersion -notmatch '^[1-9]\d*(\.\d+){0,2}$') {
    throw "APP_VERSION must have one to three numeric components and start above zero (for example 1.0.0): $AppVersion"
}

$Dist = Join-Path $RootDir 'target/distributions'
$InputDir = Join-Path $RootDir 'target/package-input'
$ImageDir = Join-Path $Dist "app-image/$AppName"
Remove-Item $Dist, $InputDir -Recurse -Force -ErrorAction SilentlyContinue
New-Item $Dist, $InputDir, (Join-Path $Dist 'app-image') -ItemType Directory -Force | Out-Null

mvn -B -ntp org.apache.maven.plugins:maven-dependency-plugin:3.8.1:copy-dependencies `
    -DincludeScope=runtime "-DoutputDirectory=$InputDir"
if ($LASTEXITCODE -ne 0) { throw 'Maven dependency copy failed.' }
Copy-Item (Join-Path $RootDir 'target/CatDinoGame-*.jar') $InputDir

$MainJar = (Get-ChildItem $InputDir -Filter 'CatDinoGame-*.jar' | Select-Object -First 1).Name
if (-not $MainJar) { throw 'The application JAR was not found.' }

jpackage `
    --type app-image `
    --name $AppName `
    --app-version $AppVersion `
    --vendor AlexMarinucci99 `
    --description 'Gioco arcade 2D con MasterCat' `
    --input $InputDir `
    --main-jar $MainJar `
    --main-class $MainClass `
    --dest (Join-Path $Dist 'app-image')
if ($LASTEXITCODE -ne 0) { throw 'jpackage app-image failed.' }

jpackage `
    --type exe `
    --name $AppName `
    --app-version $AppVersion `
    --vendor AlexMarinucci99 `
    --description 'Gioco arcade 2D con MasterCat' `
    --app-image $ImageDir `
    --dest $Dist `
    --win-menu `
    --win-shortcut `
    --win-dir-chooser
if ($LASTEXITCODE -ne 0) { throw 'jpackage EXE installer failed.' }

$Installer = Get-ChildItem $Dist -Filter '*.exe' | Select-Object -First 1
if (-not $Installer) { throw 'The Windows EXE installer was not produced.' }
Move-Item $Installer.FullName (Join-Path $Dist "CatGame-windows-x64-$AppVersion.exe")

$RuntimeDir = Join-Path $ImageDir 'runtime'
if (-not (Test-Path (Join-Path $ImageDir 'CatGame.exe')) -or -not (Test-Path $RuntimeDir)) {
    throw 'The app-image launcher or bundled Java runtime is missing.'
}

$JarPath = Join-Path $InputDir $MainJar
$Entries = @(& jar tf $JarPath)
$ResourceRoot = Join-Path $RootDir 'src/main/resources'
foreach ($Resource in Get-ChildItem $ResourceRoot -File -Recurse) {
    $RelativePath = [IO.Path]::GetRelativePath($ResourceRoot, $Resource.FullName).Replace('\', '/')
    if ($Entries -notcontains $RelativePath) { throw "Missing packaged game resource: $RelativePath" }
}

Compress-Archive -Path $ImageDir -DestinationPath (Join-Path $Dist "CatGame-windows-x64-$AppVersion.zip") -CompressionLevel Optimal
if (-not (Get-ChildItem $Dist -Filter '*.exe') -or -not (Get-ChildItem $Dist -Filter '*.zip')) {
    throw 'Expected installer or portable ZIP was not produced.'
}
Get-ChildItem $Dist -File | Select-Object Name, Length
