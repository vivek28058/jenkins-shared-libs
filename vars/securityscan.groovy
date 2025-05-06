def call(String dockerUsername, String dockerPassword, String imageName, String reportDir = "reports") {
    stage('Security Scan (Python)') {
        bat "python scripts/security_scan.py ${dockerUsername} ${dockerPassword} ${imageName} ${reportDir}"
    }
}
