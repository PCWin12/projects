function [min, image]  = recogIm(a,im , mean , phi , images , nfiles)

input_image  = im;
input_image = double(input_image(:))- mean;
inputY = phi* input_image;
min = 9999;
for i= 1:1:nfiles
    
    imageY{i} = phi* images{i};
    if  findEuDist(imageY{i} , inputY)<min
        min = findEuDist(imageY{i} , inputY);
       %min = findABS(imageY{i} , inputY);
       iminc = i;
       
    end
    
    
end
%display(min);

image = images{i};

end