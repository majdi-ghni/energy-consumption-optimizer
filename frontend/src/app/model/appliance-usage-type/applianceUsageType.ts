export enum ApplianceUsageType {
  CONTINUOUS_USE = 'CONTINUOUS_USE', // operate continuously, such as fridges, routers, etc.
  SPORADIC_USE = 'SPORADIC_USE', // cannot be easily scheduled, like lighting, cooking equipment, computers, etc.
  PLANNED_USE = 'PLANNED_USE', // usage can be scheduled according to preference or price signals like washing machines, dishwashers, etc.
}
