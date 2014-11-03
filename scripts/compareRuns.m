filename='..\\timeSeries.txt';
[h, allRuns]=hdrload(filename);

% plot acfs of absolute returns

hold all;

for run=1:6
    data = allRuns(find(allRuns(:,1)==run),:);

    % autocorr computes the conficence bands for acf, but we don't make use
    % of it

    [acf,lags,bounds] = autocorr(abs(data(:,4)),600,400,0);
    plot(lags(1:200), acf(1:200),'LineWidth',2);
end

title('ACF for different values of D','FontSize',14);
xlabel('Lag','FontSize',14);
ylabel('Correlatoin','FontSize',14);
legend('1','0.1','0.01','0.001','0.0001','0.00001');

hold off

filename = '..\\graphics\\comparisonOfACFs.pdf';
orient tall;
print('-dpdf', filename);


% plot histograms of returns


transposedReturns = zeros(length(data),6);
for run=1:6
    transposedReturns(:,run) = allRuns(find(allRuns(:,1)==run),4);
end

[n,xout] = hist(transposedReturns,100);
semilogy(xout, n,'LineWidth',2);
title('Return histograms for different values of D','FontSize',14);
xlabel('Return','FontSize',14);
ylabel('Number of observations','FontSize',14);

legend('1','0.1','0.01','0.001','0.0001','0.00001');

filename = '..\\graphics\\comparisonOfHistograms.pdf';
orient tall;
print('-dpdf', filename);

