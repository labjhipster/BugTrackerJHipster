import { ITicket } from 'app/entities/ticket/ticket.model';

export interface IAttachment {
  id: number;
  name?: string | null;
  file?: string | null;
  fileContentType?: string | null;
  ticket?: ITicket | null;
}

export type NewAttachment = Omit<IAttachment, 'id'> & { id: null };
