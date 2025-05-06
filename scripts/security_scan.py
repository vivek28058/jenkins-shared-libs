import os
import subprocess
import shutil

def run_command(command, shell=True):
    print(f"Running: {command}")
    result = subprocess.run(command, shell=shell)
    if result.returncode != 0:
        raise Exception(f"Command failed: {command}")

def security_scan(docker_username, docker_password, image_name, report_dir="reports"):
    print("🔐 Stage: Security Scan")

    # Docker login
    run_command(f"docker login -u {docker_username} -p {docker_password}")

    # Pull Docker image
    run_command(f"docker pull {image_name}")

    # Syft setup
    if os.path.exists("syft"):
        shutil.rmtree("syft")
    run_command("curl -sSfL https://github.com/anchore/syft/releases/download/v1.22.0/syft_1.22.0_windows_amd64.zip -o syft.zip")
    run_command('powershell -Command "Expand-Archive -Path syft.zip -DestinationPath .\\syft"')
    shutil.move(".\\syft\\syft.exe", "C:\\Windows\\System32\\syft.exe")

    # Generate SBOM
    os.makedirs(report_dir, exist_ok=True)
    run_command(f"syft {image_name} -o json > {report_dir}\\sbom-syft.json")

    # Grype setup
    if os.path.exists("grype"):
        shutil.rmtree("grype")
    run_command("curl -sSfL https://github.com/anchore/grype/releases/download/v0.91.0/grype_0.91.0_windows_amd64.zip -o grype.zip")
    run_command('powershell -Command "Expand-Archive -Path grype.zip -DestinationPath .\\grype"')
    shutil.move(".\\grype\\grype.exe", "C:\\Windows\\System32\\grype.exe")

    # Run Grype scan
    run_command("grype db update")
    run_command(f"grype {image_name} -o json > {report_dir}\\vuln-report-grype.json")

    print("✅ Security scan completed.")

# Example usage:
# security_scan("your-docker-username", "your-docker-password", "your-image-name")
