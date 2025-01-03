import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITicket, NewTicket } from '../ticket.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITicket for edit and NewTicketFormGroupInput for create.
 */
type TicketFormGroupInput = ITicket | PartialWithRequiredKeyOf<NewTicket>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITicket | NewTicket> = Omit<T, 'date'> & {
  date?: string | null;
};

type TicketFormRawValue = FormValueOf<ITicket>;

type NewTicketFormRawValue = FormValueOf<NewTicket>;

type TicketFormDefaults = Pick<NewTicket, 'id' | 'date' | 'labels'>;

type TicketFormGroupContent = {
  id: FormControl<TicketFormRawValue['id'] | NewTicket['id']>;
  title: FormControl<TicketFormRawValue['title']>;
  description: FormControl<TicketFormRawValue['description']>;
  dueDate: FormControl<TicketFormRawValue['dueDate']>;
  date: FormControl<TicketFormRawValue['date']>;
  status: FormControl<TicketFormRawValue['status']>;
  type: FormControl<TicketFormRawValue['type']>;
  priority: FormControl<TicketFormRawValue['priority']>;
  project: FormControl<TicketFormRawValue['project']>;
  assignedTo: FormControl<TicketFormRawValue['assignedTo']>;
  reportedBy: FormControl<TicketFormRawValue['reportedBy']>;
  labels: FormControl<TicketFormRawValue['labels']>;
};

export type TicketFormGroup = FormGroup<TicketFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TicketFormService {
  createTicketFormGroup(ticket: TicketFormGroupInput = { id: null }): TicketFormGroup {
    const ticketRawValue = this.convertTicketToTicketRawValue({
      ...this.getFormDefaults(),
      ...ticket,
    });
    return new FormGroup<TicketFormGroupContent>({
      id: new FormControl(
        { value: ticketRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      title: new FormControl(ticketRawValue.title, {
        validators: [Validators.required],
      }),
      description: new FormControl(ticketRawValue.description),
      dueDate: new FormControl(ticketRawValue.dueDate),
      date: new FormControl(ticketRawValue.date),
      status: new FormControl(ticketRawValue.status),
      type: new FormControl(ticketRawValue.type),
      priority: new FormControl(ticketRawValue.priority),
      project: new FormControl(ticketRawValue.project),
      assignedTo: new FormControl(ticketRawValue.assignedTo),
      reportedBy: new FormControl(ticketRawValue.reportedBy),
      labels: new FormControl(ticketRawValue.labels ?? []),
    });
  }

  getTicket(form: TicketFormGroup): ITicket | NewTicket {
    return this.convertTicketRawValueToTicket(form.getRawValue() as TicketFormRawValue | NewTicketFormRawValue);
  }

  resetForm(form: TicketFormGroup, ticket: TicketFormGroupInput): void {
    const ticketRawValue = this.convertTicketToTicketRawValue({ ...this.getFormDefaults(), ...ticket });
    form.reset(
      {
        ...ticketRawValue,
        id: { value: ticketRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): TicketFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      date: currentTime,
      labels: [],
    };
  }

  private convertTicketRawValueToTicket(rawTicket: TicketFormRawValue | NewTicketFormRawValue): ITicket | NewTicket {
    return {
      ...rawTicket,
      date: dayjs(rawTicket.date, DATE_TIME_FORMAT),
    };
  }

  private convertTicketToTicketRawValue(
    ticket: ITicket | (Partial<NewTicket> & TicketFormDefaults),
  ): TicketFormRawValue | PartialWithRequiredKeyOf<NewTicketFormRawValue> {
    return {
      ...ticket,
      date: ticket.date ? ticket.date.format(DATE_TIME_FORMAT) : undefined,
      labels: ticket.labels ?? [],
    };
  }
}
