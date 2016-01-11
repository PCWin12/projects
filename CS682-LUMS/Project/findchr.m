function [chr] = findchr(name)
chr=name;
if strcmp(name,'alpha')
    chr='a';
elseif strcmp(name,'beta')
        chr ='b';
elseif strcmp(name,'gama')
        chr = 'g';
elseif strcmp(name, 'divide')
    chr='/';
elseif strcmp(name,'bracketsClose')
    chr=')';
elseif strcmp(name,'bracketsOpen')
    chr='(';
elseif strcmp(name,'equals')
chr='=';
elseif strcmp(name, 'minus')
    chr='-';
elseif strcmp(name,'multiply')
    chr='*';
elseif strcmp(name,'pi')
    chr = 'p';
elseif strcmp(name,'plus')
    chr='+';
elseif strcmp(name,'sigma')
    chr='s';
elseif  strcmp(name,'theeta')
    chr='t';
elseif strcmp(name,'x')
    chr='x';
elseif strcmp(name,'y')
    chr='y';
elseif strcmp(name,'z')
    chr='z';
end
end