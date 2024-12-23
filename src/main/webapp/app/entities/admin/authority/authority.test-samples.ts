import { IAuthority, NewAuthority } from './authority.model';

export const sampleWithRequiredData: IAuthority = {
  name: '242c6561-808e-41e3-8a7e-cca2c73b6701',
};

export const sampleWithPartialData: IAuthority = {
  name: '63209ead-c8f9-4227-b743-a6171150ffa0',
};

export const sampleWithFullData: IAuthority = {
  name: '8d0c2fd8-e976-4838-8049-bcec4e588f66',
};

export const sampleWithNewData: NewAuthority = {
  name: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
