package forcomp

object Main extends App {

  type Word = String

  type Sentence = List[Word]

  type Occurrences = List[(Char, Int)]

  val dictionary: List[Word] = loadDictionary
 
def wordOccurrences(w: Word): Occurrences = { 
    getO(((for {
    char <- w.toLowerCase
  } yield char.toString).toList).groupBy(_.charAt(0)).toList).sorted
}

def getO(list: List[(Char, List[String])]) : Occurrences = {
  list match {
    case List() => List()
    case x :: xs => (x._1, x._2.length) :: getO(xs)
  }
}

def sentenceOccurrences(s: Sentence): Occurrences = {
  return wordOccurrences(s.mkString)
}

lazy val dictionaryByOccurrences: Map[Occurrences, List[Word]]  = {
 dictionary.groupBy (x => wordOccurrences(x))
}

def wordAnagrams(word: Word): List[Word] = {
  (dictionaryByOccurrences filter ( x => x._1 == wordOccurrences(word))).values.toList.flatten
}

////// da cambiare anche se funzionante /////
def combinations(occurrences: Occurrences): List[Occurrences] = {  

  def getListOfOccurrences(occurrences: Occurrences): List[Occurrences] = {
    occurrences.map( x => (for(i <- 1 until (x._2+1)) yield (x._1,i)).toList)
  }

  getListOfOccurrences(occurrences).foldRight(List[Occurrences](Nil))((x,y) => y ++ (for(i <- x; j <- y) yield (i :: j)))
}

def subtract(x: Occurrences, y: Occurrences): Occurrences = {
  
  
  def g(ss: Occurrences, y: Occurrences): Occurrences = ss match {
    case List() => List()
    case z :: zs => if(y.toSet.contains(z)) (z._1, z._2-1) :: g(zs, getY(y, z))  else z :: g(zs, y)
  }

  def getY(y: Occurrences, z: (Char, Int)): Occurrences = y match {
    case List() => List()
    case w :: ws => if(w == z) (w._1, w._2-1) :: getY(ws, ('?',0)) else w :: getY(ws,z)
  }

  def compactOccurrences(occurrences: Occurrences): Occurrences = {
   (occurrences filter ( x => (x._2 != 0))).groupBy( _._1 ).map( y => (y._1, y._2.map( _._2).sum ) ).toList
  }

  def spreadOccurrences(occurrences: Occurrences): Occurrences = {
    (occurrences.map( x => (for(i <- 1 until (x._2+1)) yield (x._1,1)).toList)).flatten
  }

  compactOccurrences(g(spreadOccurrences(x), spreadOccurrences(y))).sorted
}

  def sentenceAnagrams(sentence: Sentence): Unit = {

    def g(occ: Occurrences): List[Sentence] = {
      if (occ.isEmpty) {
        List(Nil)
      } else {
        val validCombinations: List[Occurrences] =  combinations(occ) filter (s => dictionaryByOccurrences contains s)
        for (x <- validCombinations;
             y <- dictionaryByOccurrences(x);
             z <- g(subtract(occ, x))) yield {y :: z}
        }
      }
      
      val w: Occurrences = sentenceOccurrences(sentence)
      g(w)
  }

}