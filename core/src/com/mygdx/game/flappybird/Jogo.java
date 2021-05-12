package com.mygdx.game.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Jogo extends ApplicationAdapter {
	private SpriteBatch batch;

	// variaveis que guardão as texturas
	private Texture[] passaros;
	private Texture fundo;

	private Texture[] canoAlto;//array dos canos superiores
	private Texture[] canoBaixo;//array dos canos inferiores


	// variaveis que guardam a altura e largura
	private float larguradispositivo;
	private float alturadispositivo;

	// variaveis que guardam movimentção do eixo x ou y
	private int movimentaçaoX = 0;
	private int movimentaçaoY = 0;

	private float variação = 0;
	private float gravidade = 0;
	private float posicaoInicialVerticalPassaro = 0;

	private float alturaEnd = 0;
	private float endposicaotela = 0;


	@Override
	public void create() {
		batch = new SpriteBatch();

		fundo = new Texture("fundo.png");//colocando a altura
		passaros = new Texture[3];
		passaros[0] = new Texture("passaro1.png");
		passaros[1] = new Texture("passaro2.png");
		passaros[2] = new Texture("passaro3.png");

		canoAlto = new Texture[2];// pegando texturas dos canos
		canoAlto[0] = new Texture("cano_topo.png");
		canoAlto[1] = new Texture("cano_topo_maior.png");

		canoBaixo = new Texture[2];//pegando texturas dos canos
		canoBaixo[0] = new Texture("cano_baixo.png");
		canoBaixo[1] = new Texture("cano_baixo_maior.png");


		alturadispositivo = Gdx.graphics.getHeight();// afirmando que a altura é a mesma que o dispositivo
		larguradispositivo = Gdx.graphics.getWidth();// afirmando que largura é a mesma que o dispositivo
		posicaoInicialVerticalPassaro = alturadispositivo / 2;
		alturaEnd = alturadispositivo - posicaoInicialVerticalPassaro / 2;// achando a altura do cano superior
		endposicaotela = (larguradispositivo / 2) * 2;// para achar o canto da direita da tela


	}

	@Override
	public void render() {
		batch.begin();


		if (variação > 3) // variando animações
		{
			variação = 0;
		}
		boolean toqueTela = Gdx.input.justTouched();// verificação de toque com bool
		if (Gdx.input.justTouched()) {
			gravidade = -25;

		}
		if (posicaoInicialVerticalPassaro > 0 || toqueTela) {
			posicaoInicialVerticalPassaro = posicaoInicialVerticalPassaro - gravidade;
		}

		batch.draw(fundo, 0, 0, larguradispositivo, alturadispositivo);// renderiza o fundão da cena

		canos();


		batch.draw(passaros[(int) variação], 50, posicaoInicialVerticalPassaro);// renderiza o player(Passaro) na cena
		variação += Gdx.graphics.getDeltaTime() * 10;

		gravidade++;
		movimentaçaoY++;// ir para frente ao iniciar
		movimentaçaoX++;// ir para cima ao iniciar

		batch.end();

	}

	@Override
	public void dispose() {

	}

	void canos() {

		batch.draw(canoAlto[0], endposicaotela - movimentaçaoX, alturaEnd - 100, 100, 900);// aparecendo os canos na tela com movimentação
		batch.draw(canoBaixo[0], endposicaotela - movimentaçaoX, 0, 100, 900);// aparecendo os canos na tela com movimentação


	}

}