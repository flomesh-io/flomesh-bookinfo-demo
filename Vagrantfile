$vm_box = "ubuntu/focal64"
$k3s_ip = "192.168.77.100"

Vagrant.configure("2") do |config|
	config.vm.define "bookinfo", primary: true do |k3s|
		k3s.vm.box = $vm_box

		k3s.vm.box_check_update = false
	  
		k3s.vm.hostname = "bookinfo"
	  
		k3s.vm.network "private_network", ip: $k3s_ip

		k3s.vm.provider "virtualbox" do |vb|
			vb.name = "bookinfo"
			vb.memory = 8192 #$vm_memory
			vb.cpus = 2 #$vm_cpus
			vb.gui = false
		end

# 		k3s.vm.synced_folder ".", "/vagrant/ingress-pipy"

		k3s.vm.provision "shell", inline: <<-SHELL
			#update to latest packages
			sed -i 's/archive.ubuntu.com/mirrors.aliyun.com/g' /etc/apt/sources.list
			sed -i 's/security.ubuntu.com/mirrors.aliyun.com/g' /etc/apt/sources.list
			apt-get -y update

			#turn off firewall
			ufw disable

			# install required packages
			apt-get install -y git wget curl bash-completion

			#install k3s
			#export INSTALL_K3S_VERSION=v1.20.6+k3s1
			export INSTALL_K3S_VERSION=v1.19.10+k3s1
			#export INSTALL_K3S_VERSION=v1.18.18+k3s1
			#export INSTALL_K3S_VERSION=v1.17.17+k3s1
			#export INSTALL_K3S_VERSION=v1.16.15+k3s1
			curl -sfL https://get.k3s.io | sh -s - --disable traefik --write-kubeconfig-mode 644

      # install bash completion for kubectl
			echo 'source /usr/share/bash-completion/bash_completion' >> /root/.bashrc
			echo 'source <(kubectl completion bash)' >> /root/.bashrc
			kubectl completion bash >/etc/bash_completion.d/kubectl

      # install kustomize
			# curl -s "https://raw.githubusercontent.com/kubernetes-sigs/kustomize/master/hack/install_kustomize.sh"  | bash -s 3.8.7 /usr/bin

			tee -a /etc/environment <<-'EOF'
			LANG=en_US.UTF-8
			LC_ALL=en_US.UTF-8
			EOF
		SHELL
	end
end
