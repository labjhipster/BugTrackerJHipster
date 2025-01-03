import { IAttachment, NewAttachment } from './attachment.model';

export const sampleWithRequiredData: IAttachment = {
  id: 15526,
  name: 'ouch',
};

export const sampleWithPartialData: IAttachment = {
  id: 11307,
  name: 'covenant excited by',
  file: '../fake-data/blob/hipster.png',
  fileContentType: 'unknown',
};

export const sampleWithFullData: IAttachment = {
  id: 21727,
  name: 'hierarchy ha editor',
  file: '../fake-data/blob/hipster.png',
  fileContentType: 'unknown',
};

export const sampleWithNewData: NewAttachment = {
  name: 'fatally responsibility',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
