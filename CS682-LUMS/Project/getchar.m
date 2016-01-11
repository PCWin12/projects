function [str] = getchar(FileName,PathName)
input_image = imread(strcat(PathName,FileName));
input_image =rgb2gray( input_image);
input_image = AutoEM(input_image);
im = input_image;
 imagefiles = dir('300K L data/*.mat');  %% The dataset directory
   nfiles1 = length(imagefiles);
   min_glob = inf;
   
   
for i = 1:nfiles1
    load(imagefiles(i).name);
    
    display(imagefiles(i).name);
[min, name]  = recogIm(imagefiles(i).name,input_image, mean_image , phi , images , nfiles);


if min<min_glob
    min_glob = min; 
    im = name;
    na = tok;
end

end
display(na);
str = findchr(na);



end