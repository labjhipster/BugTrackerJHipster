import { IUser } from './user.model';

export const sampleWithRequiredData: IUser = {
  id: 20534,
  login: 'j',
};

export const sampleWithPartialData: IUser = {
  id: 5787,
  login: 'Y5@7XHjL\\hEyGK\\LIs2\\HZ',
};

export const sampleWithFullData: IUser = {
  id: 31679,
  login: '6.@CQ7FbR\\mG0Q\\"IeUo\\PG4\\8T5',
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
