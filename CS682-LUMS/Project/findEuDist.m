function out = findEuDist(a,b)
dif = a-b;
out = sqrt(sum(dif.*dif));
end