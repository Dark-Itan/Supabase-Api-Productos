# Cargar variables del .env
Get-Content .env | ForEach-Object {
    if ($_ -match '^\s*([^#][^=]+)=(.*)$') {
        [System.Environment]::SetEnvironmentVariable($matches[1].Trim(), $matches[2].Trim(), 'Process')
    }
}

Write-Host 'Variables cargadas:' -ForegroundColor Green
Write-Host "  SUPABASE_URL: $env:SUPABASE_URL"
Write-Host "  SUPABASE_KEY: $env:SUPABASE_KEY"
Write-Host ''

# Arrancar la aplicacion
./gradlew bootRun
