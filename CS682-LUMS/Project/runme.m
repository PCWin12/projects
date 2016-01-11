clear all;
clc;
folder = uigetdir();
 imagefiles = dir(strcat(folder, '\*.png'));
   nfiles1 = length(imagefiles);
    k=1;
   for i=1:nfiles1
       eq(1,k) = getchar(imagefiles(i).name ,strcat(folder,'\'));
       k=k+1;
       
       
       
   end
   display(eq);
   
   
 syms x y z p g s a b t 
ans=  solve(eq);

   