variable "aws_region" {
  type    = string
  default = "us-east-1"
}

variable "aws_profile" {
  type    = string
  default = "dev"
}
variable "source_ami" {
  type    = string
  default = "ami-0dfcb1ef8550277af"
}

variable "ssh_username" {
  type    = string
  default = "ec2-user"
}

variable "subnet_id" {
  type    = string
  default = "subnet-0c9269a584281b73d"
}
variable "aws_access_key_id" {
  type    = string
  default = env("aws_access_key_id")
}

variable "aws_secret_access_key" {
  type    = string
  default = env("aws_secret_access_key")
}
variable "ami_user" {
  type    = list(string)
  default = ["676380934095"]
}

source "amazon-ebs" "my-ami" {
  ami_name        = "csye6225_${formatdate("YYYY_MM_DD_hh_mm_ss", timestamp())}"
  ami_description = " AMI for CSYE 6225"
  instance_type   = "t2.micro"
  region          = "${var.aws_region}"
  profile         = "${var.aws_profile}"
  ssh_username    = "${var.ssh_username}"
  subnet_id       = "${var.subnet_id}"
  source_ami      = "${var.source_ami}"
  access_key      = "${var.aws_access_key_id}"
  secret_key      = "${var.aws_secret_access_key}"
  ami_users       = "${var.ami_user}"
  ami_regions = [
    var.aws_region
  ]
  aws_polling {
    delay_seconds = 120
    max_attempts  = 50
  }

  ami_block_device_mappings {
    delete_on_termination = true
    device_name           = "/dev/xvda"
    volume_size           = 8
    volume_type           = "gp2"
  }
}

build {
  name = "build-packer"
  sources = [
    "source.amazon-ebs.my-ami"
  ]

  provisioner "shell" {
    script = "script.sh"
  }

  provisioner "file" {
    source      = "webApp-0.0.1-SNAPSHOT.jar"
    destination = "webApp-0.0.1-SNAPSHOT.jar"
  }


  provisioner "file" {
    source      = "webservice.service"
    destination = "/tmp/"
  }

  provisioner "shell" {
    inline = [
      "sudo cp /tmp/webservice.service /etc/systemd/system",
      "sudo systemctl start webservice.service",
      "sudo systemctl enable webservice.service",
      "sudo systemctl restart webservice.service",
      "sudo systemctl status webservice.service",
      "echo '****** Copied webservice! *******'"
    ]
  }

}
