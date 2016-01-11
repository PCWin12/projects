function [mean, phi, images , nfiles,tok ] = dataset123(a ,b )
imagefiles = dir(a);
    nfiles = length(imagefiles);    % Number of files found
    display(nfiles);
    [ tok remain] = strtok(b , '/');
   tok = strtok(remain , '/');
    %nfiles=10;
    for ii=1:nfiles
        currentfilename = imagefiles(ii).name;
        currentimage =rgb2gray( imread(strcat(b,currentfilename)));
        siz = size(currentimage);
        currentimage = AutoEM(currentimage);
        imtemp = double( currentimage(:));
        
        %  display(ii);
        images{ii} = imtemp;
        %  feature{ii} =divide(currentimage);
    end
    imarr =double(images{1});
    for i=2:nfiles
        imarr =double(images{i})+ imarr;
    end
    mean = imarr/nfiles;
    for ii=1:nfiles
        images{ii} = images{ii}-mean;
    end
    
    xm = images{1} - mean;
    coosum = (xm) * transpose(xm);
    
    for i=2:nfiles
        coosum =coosum +  (images{i} - mean)* transpose((images{i} - mean));
    end
    coo = coosum/nfiles;
    
    [V D] = eig(coo);
    k=100;
    phi =transpose( V(: ,1:k));
   