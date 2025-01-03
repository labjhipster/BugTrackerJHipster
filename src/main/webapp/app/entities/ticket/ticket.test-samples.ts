import dayjs from 'dayjs/esm';

import { ITicket, NewTicket } from './ticket.model';

export const sampleWithRequiredData: ITicket = {
  id: 12568,
  title: 'actual',
};

export const sampleWithPartialData: ITicket = {
  id: 31444,
  title: 'beside worth reassuringly',
  description: 'scuffle muddy rosemary',
  date: dayjs('2025-01-03T04:53'),
  status: 'REOPENED',
  type: 'BUG',
};

export const sampleWithFullData: ITicket = {
  id: 14664,
  title: 'although aboard',
  description: 'march at',
  dueDate: dayjs('2025-01-02'),
  date: dayjs('2025-01-02T18:19'),
  status: 'IN_PROGRESS',
  type: 'BUG',
  priority: 'HIGH',
};

export const sampleWithNewData: NewTicket = {
  title: 'wee devoted',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
