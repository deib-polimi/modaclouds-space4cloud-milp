set CONTAINER;
set PROVIDER;
set CLASS_REQUEST;
set TYPE_VM;
set TIME_INT;
set COMPONENT;
param ProbabilityToBeInComponent{CLASS_REQUEST, COMPONENT} >= 0;
param ArrRate{TIME_INT} >= 0;
param Alpha{CLASS_REQUEST} >=0, <=1;
param MaximumSR{CLASS_REQUEST, COMPONENT} >= 0;
param PartitionComponents{COMPONENT, CONTAINER} binary;
param Speed{TYPE_VM, PROVIDER, CONTAINER} >= 0;
param Cost{TYPE_VM, PROVIDER, CONTAINER} >= 0;
param MaxResponseTime{CLASS_REQUEST, COMPONENT} >=0;
param MinProv > 0 integer;
param MaxVMPerContainer > 0 integer;
param MinArrRate{PROVIDER} >= 0, <=1;

param Availability{PROVIDER} >= 0, <=1;
param MaxUnavailability >= 0, <= 1;

var X{PROVIDER} binary;
var W{TYPE_VM, PROVIDER, CONTAINER} binary;
var PartialArrRate{PROVIDER, TIME_INT} >= 0;
var AmountVM{TYPE_VM, PROVIDER, CONTAINER, TIME_INT} >= 0 integer;
minimize Total_Cost: sum{t in TIME_INT, p in PROVIDER, i in CONTAINER, v in TYPE_VM} (Cost[v, p, i]*AmountVM[v, p, i, t]);
subject to TotalNumberOfProviders: sum{p in PROVIDER} X[p] >= MinProv;
subject to ArrRatePerProvider1 {p in PROVIDER, t in TIME_INT}: X[p]*MinArrRate[p]*ArrRate[t] <= PartialArrRate[p, t];
subject to ArrRatePerProvider2 {p in PROVIDER, t in TIME_INT}: PartialArrRate[p, t] <= X[p]*ArrRate[t];
subject to Efficiency{t in TIME_INT}: sum{p in PROVIDER} PartialArrRate[p, t] = ArrRate [t];
subject to Select_Types {p in PROVIDER, i in CONTAINER}: sum{v in TYPE_VM} W[v, p, i] = X[p];
subject to VMAmount{v in TYPE_VM, p in PROVIDER, i in CONTAINER, t in TIME_INT}: W[v, p, i] <= AmountVM[v, p, i, t];
subject to VMAmount2{v in TYPE_VM, p in PROVIDER, i in CONTAINER, t in TIME_INT}: AmountVM[v, p, i, t] <= MaxVMPerContainer*W[v, p, i];
subject to Response_Time{k in CLASS_REQUEST, p in PROVIDER, i in CONTAINER, t in TIME_INT, c in COMPONENT:ProbabilityToBeInComponent[k,c]*PartitionComponents[c,i]>0}: PartialArrRate[p, t]*MaximumSR[k, c]*MaxResponseTime[k, c]*sum{d in CLASS_REQUEST, g in COMPONENT: ProbabilityToBeInComponent[d,g]*PartitionComponents[g, i]>0} (Alpha[d]*ProbabilityToBeInComponent[d, g]/MaximumSR[d, g]) + sum{v in TYPE_VM}(AmountVM[v, p, i, t]) <= MaximumSR[k, c]*MaxResponseTime[k, c]*sum{v in TYPE_VM}(AmountVM[v, p, i, t]*Speed[v, p, i]);

subject to AvailabilityConstraint: sum{p in PROVIDER} (log(1 - Availability[p]) * X[p]) <= log(MaxUnavailability);
