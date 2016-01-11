function [cos] = findCosSim(a,b)

cos =1-(dot(a,b))/(norm(a)*norm(b));


end