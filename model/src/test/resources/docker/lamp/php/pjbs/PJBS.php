<?php

/*
 * Copyright (C) 2007 lenny@mondogrigio.cjb.net
 *
 * This file is part of PJBS (http://sourceforge.net/projects/pjbs)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

class PJBS {

	private $sock;
        private $jdbc_enc;
        private $app_enc;

        public $last_search_length = 0;

	function __construct($jdbc_enc, $app_enc) {

                $fn = dirname(__FILE__).'/pjbs.conf';
                $conf = preg_split("/[ \t\r\n]+/", trim(file_get_contents($fn)));
		$this->sock = fsockopen($conf[0], $conf[1]);
                $this->jdbc_enc = $jdbc_enc;
                $this->app_enc = $app_enc;
 	}

	function __destruct() {
	
		fclose($this->sock);
	}

	private function parse_reply() {

		$il = explode(' ', fgets($this->sock));
		$ol = array();

		foreach ($il as $value)
			$ol[] = iconv($this->jdbc_enc, $this->app_enc, base64_decode($value));

		return $ol;
	}

	private function exchange($cmd_a) {

		$cmd_s = '';
// echo'exchange';var_dump($cmd_a);
		foreach ($cmd_a as $tok)
			$cmd_s .= base64_encode(iconv($this->app_enc, $this->jdbc_enc, $tok)).' ';
		
		$cmd_s = substr($cmd_s, 0, -1)."\n";

		fwrite($this->sock, $cmd_s);
		
		return $this->parse_reply();
	}

	public function connect($url, $user, $pass) {

		$reply = $this->exchange(array('connect', $url, $user, $pass));
// echo'connect';var_dump($reply);
		switch ($reply[0]) {

		// case 'ex':
			// return true;

		case 'ok':
			return true;

		default:
			return false;
		}
	}

	public function exec($query) {

		$cmd_a = array('exec', $query);

		if (func_num_args() > 1) {
		
			$args = func_get_args();

			for ($i = 1; $i < func_num_args(); $i ++)
				$cmd_a[] = $args[$i];
		}

		$reply = $this->exchange($cmd_a);

		switch ($reply[0]) {

		case 'ok':
			return $reply[1];

		default:
			return false;
		}
	}

	public function fetch_array($res) {

		$reply = $this->exchange(array('fetch_array', $res));
// echo'fetch_array';var_dump($reply);
		switch ($reply[0]) {

		case 'ok':
			$row = array();

			for ($i = 0; $i < $reply[1]; $i ++) {

				$col = $this->parse_reply($this->sock);
				$row[$col[0]] = $col[1];
			}

			return $row;

		default:
			return false;
		}
	}

	public function free_result($res) {

		$reply = $this->exchange(array('free_result', $res));

		switch ($reply[0]) {

		case 'ok':
			return true;
		default:
			return false;
		}
	}

	public function index($partition, $query) {

                stream_set_timeout($this->sock, 3600);

		$reply = $this->exchange(array('index', $partition, $query));

		switch ($reply[0]) {

		case 'ok':
			return true;
		default:
			return false;
		}
	}

	public function search($partition, $query, $start = 0, $length = 20) {

		$reply = $this->exchange(array('search', $partition, $query, $start, $length));

		switch ($reply[0]) {

		case 'ok':
			$res = array();

			for ($i = 0; $i < $reply[1]; $i ++) {

				$col = $this->parse_reply($this->sock);
				$res[$col[0]] = $col[1];
			}

                        $this->last_search_length = $reply[2];
			return $res;

		default:
			$this->last_search_length = 0;
                        return false;
		}
	}
}

?>
