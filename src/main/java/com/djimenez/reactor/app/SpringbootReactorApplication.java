package com.djimenez.reactor.app;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.djimenez.reactor.app.models.Comments;
import com.djimenez.reactor.app.models.User;
import com.djimenez.reactor.app.models.UserComments;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class SpringbootReactorApplication implements CommandLineRunner{

	
	private static final Logger log = LoggerFactory.getLogger(SpringbootReactorApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringbootReactorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		//fromJustExample();
		//fromIterableExample();
		//flatMapExample();
		//fluxToMono();
		//exampleUserCommentsFlatMap();
		//exampleUserCommentsZipWith();
		//exampleUserCommentsZipWith2Way();
		//exampleZipWithRanges();
		//exampleInterval();
		//exampleDelayElements();
		//exampleInfiniteInterval();
		//examplecreateIntervaleFromCreate();
		exampleContrapresion(); 
	}
	/*
	 * Creacion de Flux usando el metodo Just()
	 * Just nos permite crear un objeto Flux apartir de un conjunto de elementos
	 * 
	 * doOnNext() -> permite agregar comportamiento cuando el Flux emite unelemento
	 * 
	 * map() -> permite transformar un objeto de un tipo a otro
	 * 
	 * subscribe() -> nos permite consumir los elementos de un FLux en secuencia
	 * */
	public void fromJustExample() throws Exception {
		
		Flux<String> names = Flux.just("Diego", "Pedro", "Juan", "Maria", "Andres")
				.map(name -> name.toUpperCase())
				.doOnNext(name -> log.info("Just - Observable: " + name))
				.map(name -> name.toLowerCase());
		
		names.subscribe(name -> log.info("Just - Observador: " + name));
	}
	
	/*
	 * Creacion de flux a partir de un iterable
	 * El metodo fromIterable() nospermite crear un Flux a partir de un iterable como puede ser una lista
	 * 
	 * filter() -> nos permite realizar una evaluacion de un elemento para saber si se debe emitir o no
	 * 
	 * subscribe(consumer, errorConsumer, completeConsumer ) ->
	 *		consumer -> nos permite manejar los elementos que recibimos
	 *		errorConsumer -> nos permite manejar excepciones si la recibimos
	 *		compleateConsumer -> nos permite realizar acciones cuando termina de consumir el Flux
	 * */
	
	public void fromIterableExample() throws Exception {	
		
		List<String> userList = Arrays.asList("Diego Jimenez", "Pedro Ramirez", "Daniel Garcia", "Maria Saavedra", "Andres Gutierrez");
		Flux<String> names = Flux.fromIterable(userList);
		
		Flux<User> users = names.map(name -> new User(name.toUpperCase(), 18))
				.filter(user -> user.getName().startsWith("D"))
				.doOnNext(user -> {
					if (user == null) {
						throw new RuntimeException("Los usuarion no pueden ser nulos");
					}
					log.info("Iterable - Observable: " + user.toString());
				})
				.map(user -> {
					user.setName(user.getName().toLowerCase());
					return user;
					}
				);

		users.subscribe(user -> log.info("Iterable - Observador: " + user.toString()), 
				error -> log.error(error.getMessage()), 
				new Runnable() {
					@Override
					public void run() {
						log.info("Ha finalizado la ejecucion del observable con exito");
					}
				});
	}
	
	
	/*
	 * Metodo que hace uso del operador FlatMap()
	 * 
	 * flatMap() -> retorna un nuevo flux aplanado
	 * */
	public void flatMapExample() throws Exception {	
		
		List<String> userList = Arrays.asList("Diego Jimenez", "Pedro Ramirez", "Daniel Garcia", "Maria Saavedra", "Andres Gutierrez");

		Flux.fromIterable(userList)
			.map(user -> new User(user, 12))
			.flatMap(user -> {
				if (user.getName().startsWith("D")) {
					return Mono.just(user);
				}else {
					return Mono.empty(); 
				}
			})
			.map(user -> {
				user.setName(user.getName().toLowerCase());
				return user;
			})
			.subscribe(user -> log.info(user.toString()));		
	}
	
	/*
	 * Metodo que convierte una lista de usuarios a una de Strings 
	 * */
	public void toStringExample() throws Exception {	
		
		// Creacion de Flux desde una lista (iterable)
		List<User> userList = new ArrayList<>();
		userList.add(new User("Diego Jimenez", 24));
		userList.add(new User("Pedro Ramirez", 21));
		userList.add(new User("Daniel Garcia", 16));
		userList.add(new User("Maria Saavedra", 18));
		userList.add(new User("Andres Gutierrez", 17));

		Flux.fromIterable(userList)
			.map(user -> user.getName().toUpperCase())
			.flatMap(name -> {
				if (name.startsWith("D")) {
					return Mono.just(name);
				}else {
					return Mono.empty(); 
				}
			})
			.map(name -> name.toLowerCase())
			.subscribe(user -> log.info(user.toString()));		
	}
	
	/*
	 * Metodo que convierte un flux a un Mono
	 * 
	 * */
	public void fluxToMono() throws Exception {	
		
		
		List<User> userList = new ArrayList<>();
		userList.add(new User("Diego Jimenez", 24));
		userList.add(new User("Pedro Ramirez", 21));
		userList.add(new User("Daniel Garcia", 16));
		userList.add(new User("Maria Saavedra", 18));
		userList.add(new User("Andres Gutierrez", 17));

		Flux.fromIterable(userList)
			.collectList()
			.subscribe(list -> {
				list.forEach(item -> log.info(list.toString()));
			});		
	}
	
	/*
	 * Metodo para combinar dos flujos con el metodo FlatMap
	 * */
	public void exampleUserCommentsFlatMap() {
		Mono<User> userMono = Mono.fromCallable(() -> new User("Jhon", 40));
		Mono<Comments> commentsMono = Mono.fromCallable(() -> {
			Comments comments = new Comments();
			comments.addComment("Hi, how are you?");
			comments.addComment("where are you from?");
			comments.addComment("this is the 3rd comment");
			return comments;
		});
		
		userMono.flatMap(user -> commentsMono.map(comment -> new UserComments(user, comment)))
		.subscribe(uc -> log.info(uc.toString()));
	}
	
	/*
	 * Metodo para combinar dos flujos con el metodo zipWith
	 * */
	public void exampleUserCommentsZipWith() {
		Mono<User> userMono = Mono.fromCallable(() -> new User("Jhon", 40));
		Mono<Comments> commentsMono = Mono.fromCallable(() -> {
			Comments comments = new Comments();
			comments.addComment("Hi, how are you?");
			comments.addComment("where are you from?");
			comments.addComment("this is the 3rd comment");
			return comments;
		});
		
		Mono<UserComments> usercommentsMono = userMono.zipWith(commentsMono, (u, c) -> new UserComments(u, c));
		
		usercommentsMono.subscribe(uc -> log.info(uc.toString()));
	}
	
	/*
	 * Metodo para combinar dos flujos con el metodo zipWith, segunda forma
	 * */
	public void exampleUserCommentsZipWith2Way() {
		Mono<User> userMono = Mono.fromCallable(() -> new User("Jhon", 40));
		Mono<Comments> commentsMono = Mono.fromCallable(() -> {
			Comments comments = new Comments();
			comments.addComment("Hi, how are you?");
			comments.addComment("where are you from?");
			comments.addComment("this is the 3rd comment");
			return comments;
		});
		
		Mono<UserComments> usercommentsMono = userMono.zipWith(commentsMono)
				.map(tuple -> {
					User u = tuple.getT1();
					Comments c = tuple.getT2();
					
					return new UserComments(u, c);
				});
		
		usercommentsMono.subscribe(uc -> log.info(uc.toString()));
	}
	
	
	/*
	 * Metodo que conbina dos flujos con zipwith 
	 * 
	 * range() -> Metodo que nos permite obtener una secuancia de numeros entre un rango
	 * */
	public void exampleZipWithRanges() {
		Flux.just(1, 2, 3 ,4)
		.map(i -> (i*2))
		.zipWith(Flux.range(0, 4), (uno, dos) -> String.format("Primer Flux %d, Segundo Flux: %d", uno, dos))
		.subscribe(text -> log.info(text));
	}
	
	/*
	 * Metodo que convina un delay con un rango usando zipwith
	 * 
	 * blockLast() -> Bloquea el hilo para que no finalice hasta que el Flux termine, no es recomendable para prod ya que elimina la parte asincrona
	 * 
	 * */
	public void exampleInterval() {
		Flux<Integer> range = Flux.range(1, 12);
		Flux<Long> delay = Flux.interval(Duration.ofSeconds(1));
		
		range.zipWith(delay, (ra, de) -> ra)
		.doOnNext(i -> log.info(i.toString()))
		.blockLast();
	}
	
	/*
	 * Metodo que hace un delay por cada elemento entregado
	 * 
	 * */
	public void exampleDelayElements() {
		Flux<Integer> range = Flux.range(1, 12)
			.delayElements(Duration.ofSeconds(1))
			.doOnNext(i -> log.info(i.toString()));
		
		range.blockLast();
	}
	
	/*
	 *
	 * await() -> nos ayuda a que los hilos esperen y no finalicen hasta que se haya cumplido ciertas tareas
	 * (si el contador llega a 0 se desbloquea el hilo)
	 * 
	 * retry()-> intenta ejecutar nuevamente el flujo cada que falla, desdeel inicio
	 * */
	public void exampleInfiniteInterval() throws InterruptedException {
		
		CountDownLatch larch = new CountDownLatch(1);
		
		Flux.interval(Duration.ofSeconds(1))
		.doOnTerminate(()->larch.countDown())
		.flatMap(i -> {
			if (i >= 5) {
				return Flux.error(new InterruptedException("solo hasta 5"));
			}
			
			return Flux.just(i);
		})
		.map(i -> "Hola " + i)
		.retry(2)
		.subscribe(s -> log.info(s), e -> log.info(e.getMessage()));
		
		larch.await();
	}
	
	/*
	 * Crer un Flux desde create
	 * */
	public void examplecreateIntervaleFromCreate() throws InterruptedException {
		Flux.create(emmiter -> {
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				
				private Integer contador=0;
				
				@Override
				public void run() {
					emmiter.next(++contador);
					if (contador == 10) {
						timer.cancel();
						emmiter.complete();
					}
					if (contador == 5) {
						timer.cancel();
						emmiter.error(new InterruptedException("Error, se ha detenido el Flux"));
					}
				}
			}, 1000, 1000);
		})
		.subscribe(next -> log.info(next.toString()), 
				error -> error.getMessage(),
				() -> log.info("Hemos terminado"));
	}
	
	/*
	 * Contrapresion
	 * 
	 * Cada que nos suscribimos aun observable solicitamos la cantidad maxima que puede enviar un productor
	 * 
	 * La contrapresion nos ayuda a indicar cuantos elementos queremos solicitar
	 * podemos hacerlo a traves de implementar el subscriber o usando el operador limitRate
	 * */
	
	public void exampleContrapresion() {
		Flux.range(1, 10)
		.log()
		.limitRate(2)
		.subscribe(/*new Subscriber<Integer>() {

			private Subscription s;
			private Integer limit = 2;
			private Integer consumed = 0;
			
			@Override
			public void onSubscribe(Subscription s) {
				this.s = s;
				s.request(limit);
			}

			@Override
			public void onNext(Integer t) {
				log.info(t.toString());
				consumed++;
				if (consumed == limit) {
					consumed = 0;
					s.request(limit);
				}
			}

			@Override
			public void onError(Throwable t) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				
			}
		}*/);
		
	}
	
}