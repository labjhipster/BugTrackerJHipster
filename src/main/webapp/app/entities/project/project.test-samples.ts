import { IProject, NewProject } from './project.model';

export const sampleWithRequiredData: IProject = {
  id: 22823,
};

export const sampleWithPartialData: IProject = {
  id: 14992,
};

export const sampleWithFullData: IProject = {
  id: 1375,
  name: 'tray pack hydrolyze',
};

export const sampleWithNewData: NewProject = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
