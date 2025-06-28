
export type Item = {
  uuid: string
  productUuid: string;
  costPrice: number;
  amountAvailable: number;
  marginPercentage: number;
  name: string;
  options: Record<string, string>;
  quantity: number;
}


export type NewItem = Omit<Item, 'uuid'>;