import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';

export interface IComment {
  id: number;
  date?: dayjs.Dayjs | null;
  text?: string | null;
  login?: Pick<IUser, 'id'> | null;
  child?: IComment | null;
}

export type NewComment = Omit<IComment, 'id'> & { id: null };
