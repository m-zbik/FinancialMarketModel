filename='..\\timeSeries.txt';
[h, allRuns]=hdrload(filename);

maxRuns=190;

hursts=zeros(maxRuns,1);
inputs=zeros(maxRuns,1);

for run=1:maxRuns
    run
    data = allRuns(find((allRuns(:,1)==run) & (allRuns(:,2)>2000)),:);
    hursts(run) = estimate_hurst_exponent(data(:,6)');
    inputs(run)=mean(data(:,3));
end




points=100;
spacing=0.01;

fitx=inputs;
fity=hursts;

r=ksr(fitx,fity,spacing,points);

mins=10000000+zeros(length(r.x),1);
maxs=zeros(length(r.x),1);

for c=1:20
    th=rand(length(fitx),1);
    indicies=find(th(:)>0.2);
    tfitx=fitx(indicies);
    tfity=fity(indicies);
    r=ksr(tfitx,tfity,spacing,points);
    mins=min(mins,r.f');
    maxs=max(maxs,r.f');
end

r=ksr(fitx,fity,spacing,points);
hold on;
ciplot(mins,maxs,r.x,'red');
plot(r.x,r.f,'LineWidth',2,'Color','red');
hold off;

title('Hurst exponent for different values of D','FontSize',14);
xlabel('Value of D','FontSize',14);
ylabel('Hurst exponent','FontSize',14);

alpha(0.5)
    
filename = 'comparisonOfHurstsD.bmp';
orient landscape;
print('-dbmp', filename);

