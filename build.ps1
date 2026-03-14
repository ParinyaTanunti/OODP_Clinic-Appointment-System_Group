# Windows-compatible build script for Clinic Appointment System
# Uses explicit file paths (no globs) to avoid javac "Invalid filename" errors

$ErrorActionPreference = "Stop"
$projectRoot = $PSScriptRoot

# Explicitly list all source files in dependency order (Windows-safe, no glob issues)
$sources = @(
    "src/interfaces/ISchedulable.java",
    "src/model/Person.java",
    "src/model/Patient.java",
    "src/model/Doctor.java",
    "src/model/Appointment.java",
    "src/exception/DoctorNotFoundException.java",
    "src/exception/SlotUnavailableException.java",
    "src/exception/InvalidInputException.java",
    "src/services/FileManager.java",
    "src/services/ClinicManager.java",
    "src/main/MainApp.java"
)

# Resolve full paths and filter to existing files
$existingSources = @()
foreach ($relPath in $sources) {
    $fullPath = Join-Path $projectRoot $relPath
    if (Test-Path $fullPath) {
        $existingSources += $fullPath
    } else {
        Write-Warning "Source file not found: $relPath"
    }
}

if ($existingSources.Count -eq 0) {
    Write-Error "No source files found. Ensure the src/ directory structure exists."
    exit 1
}

# Create output directory
$outDir = Join-Path $projectRoot "out"
if (-not (Test-Path $outDir)) {
    New-Item -ItemType Directory -Path $outDir | Out-Null
}

$srcDir = Join-Path $projectRoot "src"

# Compile
Write-Host "Compiling..." -ForegroundColor Cyan
try {
    & javac -d $outDir -cp $srcDir $existingSources
    if ($LASTEXITCODE -ne 0) {
        exit $LASTEXITCODE
    }
    Write-Host "Compilation successful." -ForegroundColor Green
} catch {
    Write-Error "Compilation failed: $_"
    exit 1
}

# Run if -Run flag is passed
if ($args -contains "-Run") {
    Write-Host "Running application..." -ForegroundColor Cyan
    Push-Location $projectRoot
    try {
        & java -cp $outDir main.MainApp
    } finally {
        Pop-Location
    }
}
