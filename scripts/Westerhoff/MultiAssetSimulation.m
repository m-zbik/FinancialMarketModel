%Number of steps
T = 10000;
%Constant News
N = 0.0002;
%Model parameters
a_m = 1;
a_c = sqrt(5);
b_c = a_c;
a_f = sqrt(0.2);
b_f = a_f;
f = 1000000;
g = 1.2;
%Number of Assets
K = 5;
%Log Fundamental values
F = zeros(T,K);
%Values at T = 0;
F(1,:) = rand(1,5);
F(2,:) = F(1,:) + N;
%Log asset prices
S = zeros(T,K);
%Two periods setup required by the chartists' demand function
S(1,:) = 0;
S(2,:) = rand(1,5);
%Log price index
I = zeros(T,1);
%Markets' attractiveness
A = zeros(T,K);
%Percentage of chartists chosing market k
W = zeros(T,K);
%Chartists' demand
D_c = zeros(T,K);
%Fundamentalists demand
D_f = zeros(T,K);
%Assets and price index returns
R = zeros(T,K+1);
for t = 2:T
    s_a = 0;
    for k = 1:K
        F(t+1,k) = F(t,k) + N;
        D_f(t,k) = a_f * ((S(t,k) + b_f * (F(t,k) - S(t,k))) - S(t,k));
        D_c(t,k) = a_c * ((S(t,k) + b_c * (S(t,k) - S(t-1,k))) - S(t,k));
        A(t,k) = log(1 / (1 + (f * (F(t,k) - S(t,k))^2)));
        s_a = s_a + exp(g * A(t,k));
    end
    for k = 1:K
         W(t,k) = exp(g * A(t,k)) / s_a;
         S(t+1,k) = S(t,k) + a_m * (D_f(t,k) + (W(t,k) * D_c(t,k)));
         R(t+1,k) = S(t+1,k) - S(t,k);
    end
    I(t + 1) = log(mean(exp(S(t+1,:))));
    R(t+1, K+1) = I(t+1) - I(t); 
end

