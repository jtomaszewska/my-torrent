Aplikacja do przesyłania plików.

Funkcjonalności:
- możliwa praca dla dwóch (H2H) oraz większej liczby hostów (MH) 
- wysyłanie listy udostępnianych plików wraz z sumami kontrolnymi MD5
- ściąganie wybranych plików z hosta (przesyłanie „na żądanie” – PULL)
- przesyłanie plików do wybranego hosta (przesyłanie inicjowane przez wysyłającego -  PUSH )

Aplikacja konsolowa – komunikacja z aplikacją za pomocą linii poleceń.

Typy poleceń (wielkość liter w polecaniach jest dowolna, separatorem oddzielającym polecenie i argumenty jest spacja):
- runServer port -> polecenie uruchamia serwer aplikacji, jako argument podajemy numer portu, 
na którym serwer będzie nasłuchiwał na przychodzące połączenia,
- setWorkingDirectory path -> ustawia folder używany do pobierania i zapisywania plików,
- connect ip port -> uruchamianie połączenia z hostem o podanym ip i porcie,
- wait milisekundy -> polecenie pomocnicze pozwalające zsynchronizować komunikację pomiędzy peer’ami, 
opóźnienie dające czas na uruchomienie instancji zanim zostaną wysłane do niej polecenia,

W dalszych poleniach „peer” to unikatowa nazwa peer’a, nadawana w momencie nawiązania połączenia. 
Schemat nadawania nazw peer’om to: Peer_idHosta_kolejnyNr.
- disconnect peer -> rozłączenie połączenia z peer’em,
- filesList peer -> pobranie listy plików wraz z sumami kontrolnymi od wskazanego peer’a,
- downloadFile peer fileName1 fileName2 … filenameN -> pobranie listy plików od peer’a
- sendFile peer fileName1 fileName2 … filenameN ->  wysłanie plików do peer’a, 
- exit -> zakończenie aplikacji

Protokół warstwy aplikacji:
- protokół binarny bazujący na wbudowanych w biblioteki standardowe serializatorach ObjectInputStream i ObjectOutputStream. 

Przesyłana jest następująca struktura danych:

class Message {

  FileData[] filesList;
  RequestType requestType;
  
}

gdzie FileData to:

class FileData {

    String fileName;
    byte[] checkSum;
    byte[] data;
    
}

a RequestType:

enum RequestType {

    FILES_LIST_REQUEST,
    FILES_LIST_RESPONSE,
    FILES_SAVE,
    FILES_REQUEST,
    
}

Powyższe klasy są używane we wszystkich rodzajach komunikatów, natomiast poszczególne pola wypełniane są w zależności od potrzeb.

Uruchamianie aplikacji :
MyTorrent hostId[wymagane] skryptZPoleceniami[opcjonalne]

Przykładowe scenariusze użycia przygotowane są w załączonych skryptach:

warunki początkowe: 
w folderze D:\\TORrent_1 znajdują się pliki a.jpg i b.jpg
w D:\\TORrent_2 plik c.jpg
w D:\\TORrent_3 plik d.jpg
W trybie H2H:
Peer1: opis działania : ustawienie lokalizacji folderu roboczego i uruchomienie aplikacji host 1 na porcie 10001
Peer2: opis działania : uruchomienie aplikacji z host 2, ustawianie lokalizacji folderu roboczego, otworzenie połączenia na porcie 10001, pobranie i wypisanie listy plików od Hosta1, pobranie plików, wysłanie pliku, rozłączenie z hostem 1, zamknięcie hosta 2.

Opisane wyżej działanie aplikacji realizują skrypty: H2Hp1skrypt, H2Hp2skrypt uruchamiane w H2Hp1, H2Hp2.
W trybie MultiHost
uruchamianie skryptów MH1, MH2, MH3.
