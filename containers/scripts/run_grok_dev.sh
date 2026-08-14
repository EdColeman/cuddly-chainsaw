# remove general ssh keys to restrict access to api dev deploy key 

ssh-add -d ~/.ssh/gh_id_rsa

container run \
   -v ~/workspace/donut-playground:/donut-playground \
   -v ~/workspace/data/donut-data:/donut-data \
   --network dev-network --ssh --cpus 5 --memory 4G -it api-grok-image-1:latest /bin/bash   
