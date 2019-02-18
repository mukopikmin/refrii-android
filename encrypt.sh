#! /bin/sh

set -e

temp_dir=temp
filename=secretfiles.tar.gz
targets=(app/google-services.json keystore.jks)

mkdir -p $temp_dir
for target in "${targets[@]}"
do
  cp $target $temp_dir 
done

tar -zcvf $filename $temp_dir/*
travis encrypt-file $filename

rm -rf temp
rm -rf $filename
