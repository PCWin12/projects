function newim =  AutoEM(im)
%% Code taken from Web, Please refer to report for Reference 
[mask,mu,v,p]=EMSeg(im,2) ;
%%
std = sqrt(v);
x2 = (-(1)/(2*v(1,1)))+((1)/(2*v(1,2)));
x1 = (mu(1,1)/v(1,1)) - (mu(1,2)/v(1,2));
x0 = ((-mu(1,1)^2))/(2*v(1,1))+ ((mu(1,2)^2))/(2*v(1,2)) - log(std(1,1))+log(std(1,2));

syms x;
ans = solve(x2*x^2 + x1*x + x0 == 0,x);
if ans(1,1)>0 && ans(1,1)<255 && ans(2,1)>0 && ans(2,1)<255
ans = min(min(double(ans)));
else
if ans(1,1)>0 && ans(1,1)<255
    
    ans = ans(1,1);

else
    ans = ans(2,1);
end
end
ans = double(ans);
siz = size(im);
  for i=1:siz(1,1)
        for j =1:siz(1,2)
            if im(i,j)>ans
                newim(i,j) = 255;
            else
                newim(i,j) = 0;
        end
        end
    
%imshow(newim);
end
