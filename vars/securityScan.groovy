def call(String dockerUsername, String dockerPassword, String imageName, String reportDir = "reports") {
    stage('Security Scan') {
        // Docker Login
        bat "docker login -u ${dockerUsername} -p ${dockerPassword}"

        // Pull Image
        bat "docker pull ${imageName}"

        // Install Syft & Generate SBOM
        bat '''
            rmdir /s /q syft
            curl -sSfL https://github.com/anchore/syft/releases/download/v1.22.0/syft_1.22.0_windows_amd64.zip -o syft.zip
            powershell -Command "Expand-Archive -Path syft.zip -DestinationPath .\\syft"
            move .\\syft\\syft.exe C:\\Windows\\System32\\syft.exe
        '''
        bat "if not exist ${reportDir} mkdir ${reportDir}"
        bat "syft ${imageName} -o json > ${reportDir}\\sbom-syft.json"

        // Install Grype & Scan
        bat '''
            rmdir /s /q grype
            curl -sSfL https://github.com/anchore/grype/releases/download/v0.91.0/grype_0.91.0_windows_amd64.zip -o grype.zip
            powershell -Command "Expand-Archive -Path grype.zip -DestinationPath .\\grype"
            move .\\grype\\grype.exe C:\\Windows\\System32\\grype.exe
        '''
        bat "grype db update"
        bat "grype ${imageName} -o json > ${reportDir}\\vuln-report-grype.json"
    }
}
