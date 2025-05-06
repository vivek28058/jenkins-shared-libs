def call(String dockerUsername, String dockerPassword, String imageName, String reportDir = "reports") {
    stage('Security Scan (Python)') {
        bat "\"C:\Users\Vivek_S\AppData\Local\Microsoft\WindowsApps\python.exe\" scripts/security_scan.py ${dockerUsername} ${dockerPassword} ${imageName} ${reportDir}"
    }
}
