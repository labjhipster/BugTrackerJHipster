import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ITicket } from 'app/entities/ticket/ticket.model';
import { TicketService } from 'app/entities/ticket/service/ticket.service';
import { LabelService } from '../service/label.service';
import { ILabel } from '../label.model';
import { LabelFormService } from './label-form.service';

import { LabelUpdateComponent } from './label-update.component';

describe('Label Management Update Component', () => {
  let comp: LabelUpdateComponent;
  let fixture: ComponentFixture<LabelUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let labelFormService: LabelFormService;
  let labelService: LabelService;
  let ticketService: TicketService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [LabelUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(LabelUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(LabelUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    labelFormService = TestBed.inject(LabelFormService);
    labelService = TestBed.inject(LabelService);
    ticketService = TestBed.inject(TicketService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Ticket query and add missing value', () => {
      const label: ILabel = { id: 7351 };
      const tickets: ITicket[] = [{ id: 29380 }];
      label.tickets = tickets;

      const ticketCollection: ITicket[] = [{ id: 29380 }];
      jest.spyOn(ticketService, 'query').mockReturnValue(of(new HttpResponse({ body: ticketCollection })));
      const additionalTickets = [...tickets];
      const expectedCollection: ITicket[] = [...additionalTickets, ...ticketCollection];
      jest.spyOn(ticketService, 'addTicketToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ label });
      comp.ngOnInit();

      expect(ticketService.query).toHaveBeenCalled();
      expect(ticketService.addTicketToCollectionIfMissing).toHaveBeenCalledWith(
        ticketCollection,
        ...additionalTickets.map(expect.objectContaining),
      );
      expect(comp.ticketsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const label: ILabel = { id: 7351 };
      const ticket: ITicket = { id: 29380 };
      label.tickets = [ticket];

      activatedRoute.data = of({ label });
      comp.ngOnInit();

      expect(comp.ticketsSharedCollection).toContainEqual(ticket);
      expect(comp.label).toEqual(label);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILabel>>();
      const label = { id: 4199 };
      jest.spyOn(labelFormService, 'getLabel').mockReturnValue(label);
      jest.spyOn(labelService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ label });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: label }));
      saveSubject.complete();

      // THEN
      expect(labelFormService.getLabel).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(labelService.update).toHaveBeenCalledWith(expect.objectContaining(label));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILabel>>();
      const label = { id: 4199 };
      jest.spyOn(labelFormService, 'getLabel').mockReturnValue({ id: null });
      jest.spyOn(labelService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ label: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: label }));
      saveSubject.complete();

      // THEN
      expect(labelFormService.getLabel).toHaveBeenCalled();
      expect(labelService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILabel>>();
      const label = { id: 4199 };
      jest.spyOn(labelService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ label });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(labelService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTicket', () => {
      it('Should forward to ticketService', () => {
        const entity = { id: 29380 };
        const entity2 = { id: 23717 };
        jest.spyOn(ticketService, 'compareTicket');
        comp.compareTicket(entity, entity2);
        expect(ticketService.compareTicket).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
