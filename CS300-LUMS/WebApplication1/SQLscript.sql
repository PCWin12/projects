-- phpMyAdmin SQL Dump
-- version 3.5.2.2
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: May 09, 2013 at 05:51 PM
-- Server version: 5.5.27
-- PHP Version: 5.4.7

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `ass6`
--

-- --------------------------------------------------------

--
-- Table structure for table `library_resource`
--

CREATE TABLE IF NOT EXISTS `library_resource` (
  `rid` int(11) NOT NULL AUTO_INCREMENT,
  `issueDate` date NOT NULL DEFAULT '0000-00-00',
  `dueDate` date NOT NULL DEFAULT '0000-00-00',
  `issueTo` int(11) NOT NULL DEFAULT '-1',
  `fineID` int(11) NOT NULL DEFAULT '-1',
  `available` int(11) NOT NULL DEFAULT '1',
  `type` int(11) NOT NULL,
  `name` varchar(55) NOT NULL,
  PRIMARY KEY (`rid`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=13 ;

--
-- Dumping data for table `library_resource`
--

INSERT INTO `library_resource` (`rid`, `issueDate`, `dueDate`, `issueTo`, `fineID`, `available`, `type`, `name`) VALUES
(9, '2013-05-09', '2013-06-08', 0, -1, 1, 1, 'Book'),
(10, '0000-00-00', '0000-00-00', -1, -1, 1, 2, 'CP'),
(12, '0000-00-00', '0000-00-00', -1, -1, 1, 1, '');

-- --------------------------------------------------------

--
-- Table structure for table `library_user`
--

CREATE TABLE IF NOT EXISTS `library_user` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `type` int(11) NOT NULL,
  PRIMARY KEY (`uid`),
  UNIQUE KEY `uid` (`uid`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=20 ;

--
-- Dumping data for table `library_user`
--

INSERT INTO `library_user` (`uid`, `username`, `password`, `type`) VALUES
(1, 'admin', 'admin', 1),
(18, 'SIR', 'SIR', 3);

-- --------------------------------------------------------

--
-- Table structure for table `requests`
--

CREATE TABLE IF NOT EXISTS `requests` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` int(11) NOT NULL,
  `rid` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=9 ;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
