import { ITicket } from 'app/entities/ticket/ticket.model';

export interface ILabel {
  id: number;
  label?: string | null;
  tickets?: ITicket[] | null;
}

export type NewLabel = Omit<ILabel, 'id'> & { id: null };
