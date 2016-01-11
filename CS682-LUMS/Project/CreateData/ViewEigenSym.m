clear all
clc


[mean, phi, images , nfiles,tok ] = dataset123('dataset/plus/*.png' ,'dataset/plus/' );
p=1;
for l=1:8;
k=1;
image=phi(l,:);
for i =1:30
    for j=1:30
        im(i,j) = image(1,k);
        k=k+1;
    end
end
im = (im+abs(min(min(im))))/(max(max(im)));
esym{p} = im;
p=p+1;
end
for l=1:8;
subplot(4,2,l), imshow(esym{l})
end





