package org.example.retea.service;

import org.example.retea.domain.*;
import org.example.retea.domain.validators.ValidationException;
import org.example.retea.domain.validators.Validator;
import org.example.retea.repository.DataBaseRepository.*;
import org.example.retea.utils.events.ChangeEvent;
import org.example.retea.utils.events.ChangeEventType;
import org.example.retea.utils.events.StatusPrietenieType;
import org.example.retea.utils.observer.Observable;
import org.example.retea.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.StreamSupport;

public class Service implements Observable<ChangeEvent>{

    protected Validator validator;
    private static UtilizatorDatabaseRepo utilizatorRepo;
    private static MesajDatabaseRepo mesajRepo;
    private static PrietenieDatabaseRepo prietenieRepo;
    private static InvitatieDatabaseRepo invitatieRepo;
    private static ParolaDatabaseRepo parolaRepo;

    static Map<Long,Integer> viz = new HashMap<Long,Integer>();
    static int lungmax;
    static String utilizatori = "";

    public Service(Validator val, UtilizatorDatabaseRepo utilizatorRepo, PrietenieDatabaseRepo prietenieRepo, MesajDatabaseRepo mesajeRepo, InvitatieDatabaseRepo invRepo, ParolaDatabaseRepo paroleRepo)
    {
        this.utilizatorRepo = utilizatorRepo;
        this.prietenieRepo = prietenieRepo;
        this.mesajRepo = mesajeRepo;
        this.invitatieRepo = invRepo;
        this.parolaRepo = paroleRepo;
        this.validator = val;
    }

    public Page<Utilizator> getAllUsersPage(Pageable p)
    {
        return utilizatorRepo.gasesteToatePage(p);
    }
    public Page<Prietenie> getAllFriendshipsPage(Pageable p)
    {
        return prietenieRepo.gasesteToatePage(p);
    }
    public Page<Mesaj> getAllMessagesPage(Pageable p)
    {
        return mesajRepo.gasesteToatePage(p);
    }
    public Page<Invitatie> getAllInvitationsPage(Pageable p)
    {
        return invitatieRepo.gasesteToatePage(p);
    }

    public void adaugareUtilizator(String prenume, String numeFam, String numeUtil)
    {
        Optional<Utilizator> utiliz2 = utilizatorRepo.gasesteUnulNumeUtil(numeUtil);
        utiliz2.ifPresentOrElse(
                u -> {
                    throw new ValidationException("Exista deja un utilizator cu acest nume de utilizator!");
                },
                () -> {
                    Utilizator utiliz = new Utilizator(prenume, numeFam, numeUtil);
                    utilizatorRepo.salveaza(utiliz);
                    notifyObservers(new ChangeEvent(ChangeEventType.ADD, utiliz));
                }
        );
    }

    public void modificareUtilizator(String prenume, String numeFam, String numeUtil)
    {
        Optional<Utilizator> existent = utilizatorRepo.gasesteUnulNumeUtil(numeUtil);
        if(existent.isEmpty()){
            throw new ValidationException("Nu exista un utilizator cu acest nume de utilizator!");
        }
        else {
            Utilizator newUser = new Utilizator(prenume,numeFam, existent.get().getNumeUtil());
            utilizatorRepo.actualizeaza(newUser);
            notifyObservers(new ChangeEvent(ChangeEventType.UPDATE, newUser));
        }
    }

    public void stergereUtilizator(Long ID)
    {
        Optional<Utilizator> utiliz = utilizatorRepo.gasesteUnul(ID);
        utiliz.ifPresentOrElse(
                userToDelete -> {
                    utilizatorRepo.sterge(ID);
                    notifyObservers(new ChangeEvent(ChangeEventType.DELETE, userToDelete));
                },
                () -> { throw new ValidationException("Utilizatorul nu exista!"); }
        );
    }

    public Optional<Utilizator> cautareUtilizator(Long id)
    {
        Optional<Utilizator> utiliz = utilizatorRepo.gasesteUnul(id);
        if(utiliz.isPresent())
            return utiliz;
        else
            throw new ValidationException("Utilizatorul nu exista!");
    }

    public Optional<Utilizator> cautareNumeUtilUtilizator(String numeUtil)
    {
        Optional<Utilizator> utiliz = utilizatorRepo.gasesteUnulNumeUtil(numeUtil);
        return utiliz;
    }

    public void adaugarePrieten(Long ID, Long prietenId)
    {
        Optional<Utilizator> utilizOptional1 = utilizatorRepo.gasesteUnul(ID);
        Optional<Utilizator> utilizOptional2 = utilizatorRepo.gasesteUnul(prietenId);

        Optional<Prietenie> prieten1 = prietenieRepo.gasesteUnul(new Tuplu<>(ID, prietenId));
        Optional<Prietenie> prieten2 = prietenieRepo.gasesteUnul(new Tuplu<>(prietenId, ID));

        if(prieten1.isPresent() || prieten2.isPresent())
            throw new ValidationException("Aceasta prietenie exista deja!");
        else {
            utilizOptional1.ifPresentOrElse(
                    utilizator1 -> utilizOptional2.ifPresentOrElse(
                            utilizator2 -> {
                                Prietenie prietenie = new Prietenie(utilizator1, utilizator2, LocalDateTime.now());
                                prietenieRepo.salveaza(prietenie);
                                notifyObservers(new ChangeEvent(ChangeEventType.ADD, prietenie));
                            },
                            () -> { throw new ValidationException("Utilizatorul cu ID " + prietenId + " nu exista"); }
                    ),
                    () -> { throw new ValidationException("Utilizatorul cu ID " + ID + " nu exista"); }
            );
        }
    }

    public void stergerePrieten(Long ID, Long fID)
    {
        Optional<Utilizator> utilizOptional1 = utilizatorRepo.gasesteUnul(ID);
        Optional<Utilizator> utilizOptional2 = utilizatorRepo.gasesteUnul(fID);

        utilizOptional1.ifPresentOrElse(
                utiliz1 -> utilizOptional2.ifPresentOrElse(
                        utiliz2 -> {
                            Tuplu<Long, Long> tuplu = new Tuplu<>(ID, fID);
                            Optional<Prietenie> priet = prietenieRepo.gasesteUnul(tuplu);
                            if(priet.isPresent()) {
                                prietenieRepo.sterge(tuplu);
                                priet.ifPresent(prietenie -> notifyObservers(new ChangeEvent(ChangeEventType.DELETE, null, prietenie)));
                            }
                        },
                        () -> { throw new ValidationException("Utilizatorul cu ID " + fID + " nu exista!"); }
                ),
                () -> { throw new ValidationException("Utilizatorul cu ID " + ID + " nu exista!"); }
        );
    }

    public static void DFS(Long x) {
        viz.put(x, 1);
        Optional<Utilizator> utiliz = utilizatorRepo.gasesteUnul(x);
        if (utiliz.isPresent()) {
            utiliz.get().getPrieteni().forEach(p -> {
                if (viz.get(p.getId()) != 1) {
                    DFS(p.getId());
                }
            });
        }
    }

    public static int nrComunitati()
    {
        viz.clear();

        utilizatorRepo.gasesteToate().forEach(u -> viz.put(((Utilizator) u).getId(), 0));

        return (int) StreamSupport.stream(Spliterators.spliteratorUnknownSize(utilizatorRepo.gasesteToate().iterator(), 0), false)
                .filter(u -> viz.get(((Utilizator) u).getId()) == 0)
                .peek(u -> DFS(((Utilizator) u).getId()))
                .count();
    }

    public static void dfsUpdated(Long x, int[] len, StringBuilder denumiri) {
        viz.put(x, 1);
        Optional<Utilizator> utilizOptional = utilizatorRepo.gasesteUnul(x);

        if (utilizOptional.isPresent()) {
            Utilizator utiliz = utilizOptional.get();
            len[0] = len[0] + 1;

            denumiri.append(utiliz.getPrenume()).append(" ").append(utiliz.getNumeFam()).append(",");

            if (len[0] > lungmax) {
                lungmax = len[0];
                utilizatori = denumiri.toString();
            }

            utiliz.getPrieteni().forEach(prieten -> {
                if (viz.get(prieten.getId()) != 1) {
                    dfsUpdated(prieten.getId(), len, denumiri);
                }
            });
        }
    }

    public static String comunitateSociabila() {
        String ceaMaiSociabilaComunitate = "";
        int lungMax = 0;

        Iterable<Utilizator> listaUtilizatori = utilizatorRepo.gasesteToate();
        for (Utilizator user : listaUtilizatori) {
            viz.clear();
            utilizatorRepo.gasesteToate().forEach(u -> viz.put(((Utilizator) u).getId(), 0));
            int[] lung = new int[]{0};
            StringBuilder denumiri = new StringBuilder();
            dfsUpdated(((Utilizator) user).getId(), lung, denumiri);

            if (lung[0] > lungMax) {
                lungMax = lung[0];
                ceaMaiSociabilaComunitate = denumiri.toString();
            }
        }
        return ceaMaiSociabilaComunitate;
    }

    public void adaugareMesaj(Long id1, Long id2, String text, LocalDateTime data, Long raspuns)
    {
        Mesaj msg = new Mesaj(id1, id2, text, data);
        msg.setRaspuns(raspuns);
        mesajRepo.salveaza(msg);
        notifyObservers(new ChangeEvent(ChangeEventType.ADD, msg));
    }

    public Optional<Mesaj> cautareMesaj(Long id)
    {
        Optional<Mesaj> m = mesajRepo.gasesteUnul(id);
        if(m.isPresent())
            return m;
        else
            throw new ValidationException("Mesajul nu exista!");
    }

    public static String afisareMesaje(Long idUtiliz1, Long idUtiliz2)
    {
        Iterable<Mesaj> mesaje =  mesajRepo.conversatiiUtilizatori(idUtiliz1, idUtiliz2);
        StringBuilder rezultat = new StringBuilder();

        for (Mesaj mesaj : mesaje) {
            rezultat.append(mesaj.toString());
            rezultat.append(System.lineSeparator());
        }
        return rezultat.toString();
    }

    public static Iterable<Mesaj> afisareListaMesaje(Long idUtiliz1, Long idUtiliz2)
    {
        return mesajRepo.conversatiiUtilizatori(idUtiliz1, idUtiliz2);
    }

    public void adaugareInvitatie(Long ID, Long prietenId, StatusPrietenieType status)
    {
        Optional<Utilizator> utilizOptional1 = utilizatorRepo.gasesteUnul(ID);
        Optional<Utilizator> utilizOptional2 = utilizatorRepo.gasesteUnul(prietenId);
        Optional<Invitatie> invit = invitatieRepo.gasesteUnul(new Tuplu<>(ID, prietenId));

        if(invit.isPresent())
            throw new ValidationException("Aceasta invitatie exista deja!");
        else {
            utilizOptional1.ifPresentOrElse(
                    utiliz1 -> utilizOptional2.ifPresentOrElse(
                            utiliz2 -> {
                                Invitatie inv = new Invitatie(ID, prietenId, status);
                                invitatieRepo.salveaza(inv);
                                notifyObservers(new ChangeEvent(ChangeEventType.ADD, inv));
                                if(status == StatusPrietenieType.accepted)
                                    adaugarePrieten(ID, prietenId);
                            },
                            () -> { throw new ValidationException("Utilizatorul cu ID " + prietenId + " nu exista!"); }
                    ),
                    () -> { throw new ValidationException("Utilizatorul cu ID " + ID + " nu exista!"); }
            );
        }
    }

    public void modificareInvitatie(Long id1, Long id2, StatusPrietenieType status)
    {
        Optional<Utilizator> utilizOptional1 = utilizatorRepo.gasesteUnul(id1);
        Optional<Utilizator> utilizOptional2 = utilizatorRepo.gasesteUnul(id2);
        Optional<Invitatie> invit = invitatieRepo.gasesteUnul(new Tuplu<>(id1, id2));

        if(!invit.isPresent())
            throw new ValidationException("Aceasta invitatie nu exista!");
        else {
            utilizOptional1.ifPresentOrElse(
                    utiliz1 -> utilizOptional2.ifPresentOrElse(
                            utiliz2 -> {
                                Invitatie inv = new Invitatie(id1, id2, status);
                                invitatieRepo.actualizeaza(inv);
                                notifyObservers(new ChangeEvent(ChangeEventType.UPDATE, inv));
                                if(status  == StatusPrietenieType.accepted)
                                    adaugarePrieten(id1, id2);
                            },
                            () -> { throw new ValidationException("Utilizatorul cu ID " + id1 + " nu exista!"); }
                    ),
                    () -> { throw new ValidationException("Utilizatorul cu ID " + id2 + " nu exista!"); }
            );
        }
    }

    public Iterable<Utilizator> cautareUtilizatoriNeinvitati(Long id)
    {
        return utilizatorRepo.gasesteTotiNeinvitatiDeID(id);
    }

    public Iterable<Utilizator> cautareUtilizatoriPending(Long id)
    {
        return utilizatorRepo.gasesteToatePending(id);
    }

    public Iterable<String> getFriendsForUser(Long id)
    {
        return utilizatorRepo.gasesteTotiPrieteniiPentruUtiliz(id);
    }

    public Optional<Parola> cautareParola(Parola parola) throws ValidationException
    {
        Optional<Parola> par = parolaRepo.gasesteUnul(new Tuplu<>(parola.getNumeUtil(), parola.getParola()));
        if(par.isPresent())
            return par;
        else
            throw new ValidationException("Parola incorecta!");
    }

    public void adaugare_parola(String numeUtil, String parola)
    {
        Parola p = new Parola(parola, numeUtil);
        parolaRepo.salveaza(p);
    }

    private List<Observer<ChangeEvent>> observers = new ArrayList<>();
    @Override
    public void addObserver(Observer<ChangeEvent> e) {
        observers.add(e);
    }
    @Override
    public void removeObserver(Observer<ChangeEvent> e) {
        observers.remove(e);
    }
    @Override
    public void notifyObservers(ChangeEvent t) {
        observers.forEach(observer -> observer.update(t, this));
    }
}
